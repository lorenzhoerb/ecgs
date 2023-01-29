import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { filter, first, interval, Observable, Subject, Subscription } from 'rxjs';
import { GradeDto } from '../dtos/gradeDto';
import { MessageErrorDto, MessageErrorType } from '../dtos/messageErrorDto';
import { Globals } from '../global/globals';
import { AuthService } from './auth.service';
import { StompService } from './stomp.service';
import { v4 as uuid } from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class GradeService {

  public messageErrors$: Subject<MessageErrorDto>;
  public judgeEvent$: Subject<JudgeInfoDto>;
  public gradeEvent$: Subject<GradeDto>;

  private gradBaseUri: string = this.globals.backendUri + '/grades';

  private activeSubscriptions: SaveSubscription<GradeSubscription | JudgeSubscription>[] = [];
  private errorSubscription?: Subscription;

  constructor(private httpClient: HttpClient,
    private globals: Globals,
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private toastr: ToastrService,
    private stomp: StompService) {

    this.messageErrors$ = new Subject();
    this.judgeEvent$ = new Subject();
    this.gradeEvent$ = new Subject();

    this.initialize();
  }

  /////////////////////////////
  ////SECTION - HTTP
  /////////////////////////////

  /**
   * Loads specific competition from the backend
   *
   * @param competitionId of competition to load
   */
  getAllGrades(competitionId: number, gradingGroupId: number, stationId: number): Observable<GradeDto[]> {
    console.log(`Load all grades for ${competitionId}/${gradingGroupId}/${stationId}`);
    return this.httpClient.get<GradeDto[]>(`${this.gradBaseUri}/${competitionId}/${gradingGroupId}/${stationId}`);
  }

  kekus(): Observable<GradeDto[]> {
    return this.httpClient.get<GradeDto[]>(`${this.gradBaseUri}/kek`);
  }

  /////////////////////////////
  ////!SECTION - HTTP
  /////////////////////////////


  /////////////////////////////
  ////SECTION - WEBSOCKETS
  /////////////////////////////


  //// SECTION - Public Interface

  public closeAll() {
    for (const active of this.activeSubscriptions) {
      active.subscription.unsubscribe();
    }
    this.activeSubscriptions = [];
    if (this.errorSubscription !== null && this.errorSubscription !== undefined) {
      this.errorSubscription.unsubscribe();
    }
    this.errorSubscription = null;

    this.stomp.stop();
  }

  public initialize() {
    this.stomp.start();

    this.stomp.started$.pipe(
      filter(val => val),
      first()
    ).subscribe(_ => {
      this.errorSubscription = this.stomp.watch(
        `/user/queue/error`,
        this.wsHeader()).subscribe(err => {
          console.log(err);
          this.messageErrors$.next(JSON.parse(err.body));
        });
    });
  }


  /**
   *
   * ANCHOR - Enter Station
   *
   * @param competitionId
   * @param gradingGroupId
   * @param stationId
   * @param stationName
   */
  public enterStation(competitionId: number, gradingGroupId: number, stationId: number, stationName: string): void {
    this.watchGrades(competitionId, stationName, gradingGroupId);
    this.watchJudges(competitionId, gradingGroupId, stationId);
  }

  /**
   *
   * ANCHOR - Leave Station
   *
   * @param competitionId
   * @param gradingGroupId
   * @param stationId
   * @param stationName
   */
  public leaveStation(competitionId: number, gradingGroupId: number, stationId: number, stationName: string): void {
    this.unWatchGrades(competitionId, stationName, gradingGroupId);
    this.unWatchJudges(competitionId, gradingGroupId, stationId);
  }

  /**
   *
   * ANCHOR - Judge Greeting
   *
   * @param competitionId
   * @param gradingGroupId
   * @param stationId
   */
  public sendJudgeGreeting(competitionId: number, gradingGroupId: number, stationId: number) {
    if (!this.isPermited({
      competitionId,
      gradingGroupId,
      stationId
    })) {
      return;
    }

    this.stomp.publish(
      {
        headers: this.wsHeader(),
        destination: `/app/judge/${competitionId}/${gradingGroupId}/${stationId}`
      }
    );
  }


  /**
   * enter a grade
   *
   * ANCHOR - Enter Grade
   *
   * @param grade grade to publish
   * @param stationName name of station
   */
  public enterGrade(grade: GradeDto, stationName: string) {
    if (!this.isPermited({
      competitionId: grade.competitionId,
      gradingGroupId: grade.gradingGroupId,
      stationName
    })) {
      this.messageErrors$.next({
        uuid: grade.uuid,
        type: MessageErrorType.notConnected,
        message: 'Nicht verbunden'
      });
      return;
    }

    this.stomp.publish({
      headers: this.wsHeader(),
      destination: `/app/grade/${grade.competitionId}/${grade.gradingGroupId}/${stationName}`,
      body: JSON.stringify(grade)
    });
  }

  isPermited(info: GradeSubscription | JudgeSubscription): boolean {
    //return this.activeSubscriptions.find()
    if ('stationName' in info) {
      return this.activeSubscriptions.find(active => active.type === SubscriptionType.grade
        && (active.info as GradeSubscription).competitionId === info.competitionId
        && (active.info as GradeSubscription).gradingGroupId === info.gradingGroupId
        && (active.info as GradeSubscription).stationName === info.stationName) !== undefined;
    } else if ('stationId' in info) {
      return this.activeSubscriptions.find(active => active.type === SubscriptionType.judge
        && (active.info as JudgeSubscription).competitionId === info.competitionId
        && (active.info as JudgeSubscription).gradingGroupId === info.gradingGroupId
        && (active.info as JudgeSubscription).stationId === info.stationId) !== undefined;
    }

    return false;
  }


  /**
   *
   * ANCHOR UUID
   *
   * @returns a fresh uuid for message creation
   */
  public createUuid(): string {
    return uuid();
  }


  //// !SECTION - Public Interface


  //// SECTION - Judges

  /**
   *
   * ANCHOR - Watch Judges
   *
   * @param competitionId
   * @param gradingGroupId
   * @param stationId
   */
  private watchJudges(competitionId: number, gradingGroupId: number, stationId: number): void {
    const obs1 = this.stomp.watch(
      `/topic/judge/${competitionId}/${gradingGroupId}/${stationId}`,
      this.wsHeader());

    const subs1 = obs1.subscribe((msg => {
      this.judgeEvent$.next({
        type: InfoType.hello,
        id: JSON.parse(msg.body),
        belongsTo: {
          competitionId,
          gradingGroupId,
          stationId
        }
      });
    }).bind(this));

    this.activeSubscriptions.push({
      subscription: subs1,
      type: SubscriptionType.judge,
      info: {
        competitionId,
        gradingGroupId,
        stationId
      }
    });

    const obs2 = this.stomp.watch(
      `/topic/goodbye-judge/${competitionId}/${gradingGroupId}/${stationId}`,
      this.wsHeader());

    const subs2 = obs2.subscribe((msg => {
      this.judgeEvent$.next({
        type: InfoType.goodbye,
        id: JSON.parse(msg.body),
        belongsTo: {
          competitionId,
          gradingGroupId,
          stationId
        }
      });
    }).bind(this));

    this.activeSubscriptions.push({
      subscription: subs2,
      type: SubscriptionType.judge,
      info: {
        competitionId,
        gradingGroupId,
        stationId
      }
    });

    const schedule = interval(2 * 60 * 1000);

    this.sendJudgeGreeting(competitionId, gradingGroupId, stationId);

    const subs3 = schedule.subscribe((() => this.sendJudgeGreeting(competitionId, gradingGroupId, stationId)).bind(this));

    this.activeSubscriptions.push({
      subscription: subs3,
      type: SubscriptionType.scheduled,
      info: {
        competitionId,
        gradingGroupId,
        stationId
      }
    });
  }

  /**
   *
   * ANCHOR - Unwatch Judges
   *
   * @param competitionId
   * @param gradingGroupId
   * @param stationId
   */
  private unWatchJudges(competitionId: number, gradingGroupId: number, stationId: number): void {
    this.stomp.publish(
      {
        headers: this.wsHeader(),
        destination: `/app/goodbye-judge/${competitionId}/${gradingGroupId}/${stationId}`
      }
    );

    this.unsubscribe({
      competitionId,
      gradingGroupId,
      stationId
    }, SubscriptionType.judge);

    this.unsubscribe({
      competitionId,
      gradingGroupId,
      stationId
    }, SubscriptionType.scheduled);
  }

  //// !SECTION - Judges



  //// SECTION - Grades

  /**
   *
   * ANCHOR - Watch Grades
   *
   * @param competitionId
   * @param stationName
   * @param gradingGroupId
   */
  private watchGrades(competitionId: number, stationName: string, gradingGroupId?: number): void {
    if (gradingGroupId !== null) {
      const obser = this.stomp.watch(
        `/topic/grades/${competitionId}/${gradingGroupId}/${stationName}`,
        this.wsHeader()
      );

      const subs = obser.subscribe((msg => {
        this.gradeEvent$.next(JSON.parse(msg.body));
      }).bind(this));

      this.activeSubscriptions.push({
        subscription: subs,
        type: SubscriptionType.grade,
        info: {
          competitionId,
          gradingGroupId,
          stationName
        }
      });
    }

  }

  /**
   *
   * ANCHOR - Unwatch Grades
   *
   * @param competitionId
   * @param stationName
   * @param gradingGroupId
   */
  private unWatchGrades(competitionId: number, stationName: string, gradingGroupId?: number): void {
    this.unsubscribe({
      competitionId,
      gradingGroupId,
      stationName
    }, SubscriptionType.grade);
  }

  //// !SECTION - Grades


  //// SECTION - Helper


  /**
   *
   * ANCHOR - Unsubscribe
   *
   * ATTENTION: type must match the respective find Type
   * for GradeSubscription use type: SubscriptionType.grade
   * for JudgeSubscription use type: SubscriptionType.judge or SubscriptionType.scheduled
   *
   * MISS-USE OF THIS IS UB!!!!!!!!
   *
   * @param find Info to select the correct subscribtions
   * @param type must match find Type (See {@link SaveSubscription})
   */
  private unsubscribe(find: GradeSubscription | JudgeSubscription, type: SubscriptionType) {
    let isCorrect = (s: SaveSubscription<GradeSubscription | JudgeSubscription>) => true;

    if (type === SubscriptionType.grade) {
      const f = find as GradeSubscription;

      isCorrect = (s) => {
        if (s.type !== SubscriptionType.grade) {
          return false;
        }
        const info = s.info as GradeSubscription;

        return info.competitionId === f.competitionId
          && info.stationName === f.stationName
          && info.gradingGroupId === f.gradingGroupId;
      };
    } else if (type === SubscriptionType.scheduled) {
      const f = find as JudgeSubscription;

      isCorrect = (s) => {
        if (s.type !== SubscriptionType.scheduled) {
          return false;
        }
        const info = s.info as JudgeSubscription;

        return info.competitionId === f.competitionId
          && info.stationId === f.stationId
          && info.gradingGroupId === f.gradingGroupId;
      };
    } else if (type === SubscriptionType.judge) {
      const f = find as JudgeSubscription;

      isCorrect = (s) => {
        if (s.type !== SubscriptionType.judge) {
          return false;
        }
        const info = s.info as JudgeSubscription;

        return info.competitionId === f.competitionId
          && info.stationId === f.stationId
          && info.gradingGroupId === f.gradingGroupId;
      };
    }

    const unsubscribeArr = this.activeSubscriptions.filter((val, index, arr) => {
      if (isCorrect(val)) {
        return true;
      }
      return false;
    });

    unsubscribeArr.forEach(x => {
      const res = this.activeSubscriptions.findIndex(y => isCorrect(y));
      if (res !== -1) {
        this.activeSubscriptions.splice(res, 1);
      }
      x.subscription.unsubscribe();
    });
  }

  //// !SECTION - Helper


  /////////////////////////////
  ////!SECTION - WEBSOCKETS
  /////////////////////////////

  private wsHeader() {
    return {
      //eslint-disable-next-line @typescript-eslint/naming-convention
      Authorization: this.authService.getToken()
    };
  }
}



///!SECTION


export class JudgeInfoDto {
  type: InfoType;
  id: number;
  belongsTo: JudgeSubscription;
}

export enum InfoType {
  hello,
  goodbye
}



/////////////////////////////
////SECTION - Internal Utility Classes
/////////////////////////////



/**
 * ATTENTION: type must match the respective T
 * for GradeSubscription use type: SubscriptionType.grade
 * for JudgeSubscription use type: SubscriptionType.judge or SubscriptionType.scheduled
 *
 * MISS-USE OF THIS IS UB!!!!!!!!
 */
class SaveSubscription<T> {
  subscription: Subscription;
  type: SubscriptionType;
  info: T;
}

enum SubscriptionType {
  grade,
  judge,
  scheduled
}

class JudgeSubscription {
  competitionId: number;
  gradingGroupId: number;
  stationId: number;
}

class GradeSubscription {
  competitionId: number;
  gradingGroupId?: number;
  stationName: string;
}
