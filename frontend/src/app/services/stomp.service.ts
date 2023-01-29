import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { RxStomp, RxStompConfig, RxStompState } from '@stomp/rx-stomp';
import { IFrame, IMessage } from '@stomp/stompjs';
import jwt_decode from 'jwt-decode';
import { ToastrService } from 'ngx-toastr';
import { BehaviorSubject, filter, first, interval, map, merge, mergeMap, Observable, Subject } from 'rxjs';
import { StompErrorDto, StompErrorType } from '../dtos/StompErrorDto';
import { stompConfigTemplate } from '../template-stomp.config';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class StompService extends RxStomp {

  isError = false;
  errorIterationCount = 0;
  stopped$: BehaviorSubject<boolean>;
  started$: BehaviorSubject<boolean>;
  config?: RxStompConfig;

  public constructor(private authService: AuthService,
    private router: Router,
    private toastr: ToastrService) {
    super();

    this.stopped$ = new BehaviorSubject(true);
    this.started$ = new BehaviorSubject(false);

    const brokerURL = this.findWSBackendUrl();

    this.config = Object.assign({}, stompConfigTemplate, { brokerURL });

    this.configure(this.config);
    this.stompErrors$.subscribe(this.handleStompErrors.bind(this));
    this.webSocketErrors$.subscribe(this.handleSocketErrors.bind(this));

    this.connected$.subscribe(_ => {
      if (this.isError) {
        this.toastr.success('', 'Verbindung wieder hergestellt');
        this.isError = false;
        this.errorIterationCount = 0;
      }
    });
  }

  public start() {
    this.config = Object.assign({}, this.config, {connectHeaders: {
      //eslint-disable-next-line @typescript-eslint/naming-convention
      Authorization: localStorage.getItem('authToken')
    }});

    this.configure(this.config);

    setTimeout(() => {
      this.stopped$.pipe(
        filter(state => state),
        first()
      ).subscribe(_ => {
        this.activate();
        this.stopped$.next(false);
        this.started$.next(true);
      });
    }, 100);
  }

  public stop() {
    this.isError = false;
    this.errorIterationCount = 0;
    this.started$.next(false);

    this.deactivate()
      .then(_ => setTimeout(() => this.stopped$.next(true), 100))
      .catch(e => {
        console.log('BUG!!!!!');
        console.log(e);
    });
  }

  handleSocketErrors(evt: Event) {
    if(this.stopped$.getValue()) {
      return;
    }

    this.errorIterationCount++;
    if (this.errorIterationCount >= 10) {
      this.toastr.error('Die Verbindung kann nicht wieder hergestellt werden. Versuchen Sie es später erneut',
        'Verbindung Verloren');
      this.stop();
      this.router.navigate(['/']);
      return;
    }
    if (!this.isError) {
      this.toastr.warning('Die Verbindung zum Server wurde verloren. Wir versuchen sie wieder herzustellen...',
        'Verbindung Verloren');
    }
    if (this.isError) {
      this.toastr.info('', 'Versuche Verbindung wieder aufzubauen...');
      this.configure(Object.assign({}, this.config, { reconnectDelay: 2000 * this.errorIterationCount }));
    }
    if (evt.type === 'error') {
      this.isError = true;
    }
  }

  handleStompErrors(error: IFrame): string {
    console.log(error);
    const e: StompErrorDto = JSON.parse(error.body);
    console.log(e);
    if (e !== null && e.type === StompErrorType.unauthorized) {
      this.stop();
      this.toastr.error('Sie haben hier keine Befugnis');
      this.router.navigate(['/']);
    }
    return; //return super._correlateErrors(error);
  }

  private findWSBackendUrl(): string {
    if (window.location.port === '4200') { // local `ng serve`, backend at localhost:8080
      return 'ws:/localhost:8080/ws/grading';
    } else {
      // assume deployed somewhere and backend is available at same host/port as frontend
      return 'wss:/' + window.location.host + '/ws/grading';
    }
  }
}
