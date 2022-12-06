import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {CompetitionDetail} from '../dtos/competition-detail';

@Injectable({
  providedIn: 'root'
})
export class CompetitionService {

  private competitionBaseUri: string = this.globals.backendUri + '/competitions';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Create a competition.
   *
   * @param competition competition to create
   */
  createCompetition(competition: CompetitionDetail): Observable<CompetitionDetail> {
    return this.httpClient
      .post<CompetitionDetail>(this.competitionBaseUri, competition);
  }

}
