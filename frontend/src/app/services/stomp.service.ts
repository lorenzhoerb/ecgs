import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { RxStomp } from '@stomp/rx-stomp';
import { IFrame, IMessage } from '@stomp/stompjs';
import jwt_decode from 'jwt-decode';
import { ToastrService } from 'ngx-toastr';
import { filter, interval, map, merge, mergeMap, Observable, Subject } from 'rxjs';
import { StompErrorDto, StompErrorType } from '../dtos/StompErrorDto';
import { myRxStompConfig } from '../my-rx-stomp.config';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class StompService extends RxStomp {

  isError = false;
  errorIterationCount = 0;

  public constructor(private authService: AuthService,
    private router: Router,
    private toastr: ToastrService) {
    super();

    this.configure(myRxStompConfig);
    this.stompErrors$.subscribe(this.handleStompErrors.bind(this));
    this.webSocketErrors$.subscribe(this.handleSocketErrors.bind(this));

    this.connected$.subscribe(_ => {
      if (this.isError) {
        this.toastr.success('', 'Verbindung wieder hergestellt');
        this.isError = false;
        this.errorIterationCount = 0;
      }
    });


    this.activate();
  }

  handleSocketErrors(evt: Event) {
    this.errorIterationCount++;
    if (this.errorIterationCount >= 10) {
      this.toastr.error('Die Verbindung kann nicht wieder hergestellt werden. Versuchen Sie es später erneut',
        'Verbindung Verloren');
      this.deactivate();
      this.router.navigate(['/']);
      return;
    }
    if (!this.isError) {
      this.toastr.warning('Die Verbindung zum Server wurde verloren. Wir versuchen sie wieder herzustellen...',
        'Verbindung Verloren');
    }
    if (this.isError) {
      this.toastr.info('', 'Versuche Verbindung wieder aufzubauen...');
      this.configure(Object.assign({}, myRxStompConfig, { reconnectDelay: 2000 * this.errorIterationCount }));
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
      this.deactivate();
      this.toastr.error('Sie wurden abgemeldet');
      this.router.navigate(['/']);
    }
    return; //return super._correlateErrors(error);
  }
}
