import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserDetail} from '../dtos/user-detail';
import {Competition} from '../dtos/competition';
import {CompetitionDetail} from '../dtos/competition-detail';
import {Observable, map} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class CompetitionService {

  private competitionBaseUri: string = this.globals.backendUri + '/competitions';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads specific competition from the backend
   *
   * @param id of competition to load
   */
  getCompetitionById(id: number): Observable<Competition> {
    console.log('Load competition details for ' + id);
    return this.httpClient.get<Competition>(this.competitionBaseUri + '/' + id)
      .pipe(
        map((data: Competition) => {
          data.beginOfRegistration = new Date(data.beginOfRegistration);
          data.endOfRegistration = new Date(data.endOfRegistration);
          data.endOfCompetition = new Date(data.endOfCompetition);
          data.beginOfCompetition = new Date(data.beginOfCompetition);
          return data;
        })
      );
  }

  /*
   * Create a competition.
   *
   * @param competition competition to create
   */
  createCompetition(competition: CompetitionDetail): Observable<CompetitionDetail> {
    return this.httpClient
      .post<CompetitionDetail>(this.competitionBaseUri, competition);
  }

  /**
   * Get aprticipants of competition.
   *
   * @param id id of competition to get participants for
   */
  getParticipants(id: number): Observable<Array<UserDetail>> {
    return this.httpClient
      .get<Array<UserDetail>>(this.competitionBaseUri + '/' + id + '/participants')
        .pipe(
          map((data: Array<UserDetail>) => {
            console.log(data.length);
            for(const d of data) {
              d.dateOfBirth = new Date(d.dateOfBirth);
              console.log(d.dateOfBirth);
            }
            return data;
          })
        );
  }

}
