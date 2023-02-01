import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, Subject, Subscription } from 'rxjs';
import { GradeDto } from '../dtos/gradeDto';
import { LiveResultDto } from '../dtos/liveResultDto';
import { MessageErrorDto } from '../dtos/messageErrorDto';
import { StompErrorType } from '../dtos/StompErrorDto';
import { Globals } from '../global/globals';
import { AuthService } from './auth.service';
import { StompService } from './stomp.service';

@Injectable({
  providedIn: 'root'
})
export class LiveResultsService {

  public messageErrors$: Subject<MessageErrorDto>;
  public resultEvent$: Subject<LiveResultDto>;

  private gradBaseUri: string = this.globals.backendUri + '/grades';

  private errorSubscription?: Subscription;
  private resultsSubscription?: Subscription = null;
  private customStompErrorSubs?: Subscription = null;

  constructor(private httpClient: HttpClient,
    private globals: Globals,
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private toastr: ToastrService,
    private stomp: StompService) {

    this.messageErrors$ = new Subject();
    this.resultEvent$ = new Subject();
  }

  /////////////////////////////
  ////SECTION - HTTP
  /////////////////////////////


  /**
   * Loads specific competition from the backend
   *
   * @param competitionId of competition to load
   */
  getAllResults(competitionId: number): Observable<LiveResultDto[]> {
    console.log(`Load all results for ${competitionId}`);
    return this.httpClient.get<LiveResultDto[]>(`${this.gradBaseUri}/live-results/${competitionId}`);
  }

  /////////////////////////////
  ////!SECTION - HTTP
  /////////////////////////////


  /////////////////////////////
  ////SECTION - WEBSOCKETS
  /////////////////////////////


  //// SECTION - Public Interface

  public closeAll() {
    if (this.resultsSubscription !== null) {
      this.resultsSubscription.unsubscribe();
    }
    this.resultsSubscription = null;

    if (this.customStompErrorSubs !== null) {
      this.customStompErrorSubs.unsubscribe();
    }
    this.customStompErrorSubs = null;

    if (this.errorSubscription !== null && this.errorSubscription !== undefined) {
      this.errorSubscription.unsubscribe();
    }
    this.errorSubscription = null;

    this.stomp.stop();
  }

  public initialize(competitionId: number) {
    this.closeAll();

    this.customStompErrorSubs = this.stomp.customStompError$.subscribe(e => {
      if (e !== null && e.type === StompErrorType.unauthorized) {
        this.stomp.stop();
        this.toastr.info('Melden Sie sich an um alle Informationen zu sehen.');

      }
    });

    this.stomp.start();

    this.errorSubscription = this.stomp.watch(
      `/user/queue/error`,
      this.wsHeader()).subscribe(err => {
        console.log(err);
        this.messageErrors$.next(JSON.parse(err.body));
      });

    const obser = this.stomp.watch(
      `/topic/live-results/${competitionId}`,
      this.wsHeader()
    );

    this.resultsSubscription = obser.subscribe((msg => {
      this.resultEvent$.next(JSON.parse(msg.body));
    }).bind(this));
  }

  //// !SECTION - Public Interface

  private wsHeader() {
    return {
      //eslint-disable-next-line @typescript-eslint/naming-convention
      Authorization: this.authService.getToken()
    };
  }
}
