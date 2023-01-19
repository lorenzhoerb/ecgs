import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {UserDetail} from '../dtos/user-detail';
import {Competition} from '../dtos/competition';
import {CompetitionDetail} from '../dtos/competition-detail';
import {map, Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {SimpleGradingGroup} from '../dtos/simple-grading-group';

import {ParticipantResult, ParticipantResultDTO, SimpleCompetitionListDto} from '../dtos/simpleCompetitionListDto';
import { CompetitionSearchDto } from '../dtos/competitionSearchDto';
import {GradingGroupWithRegisterToDto} from '../dtos/gradingGroupWithRegisterToDto';
import {PartFilterDto} from '../dtos/part-filter-dto';
import {ParticipantManageDto} from '../dtos/participant-manage-dto';
import {Pageable} from '../dtos/pageable';

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

  /**
   * Loads specific competition from the backend with more detailed information.
   * Special access rights needed.
   *
   * @param id of competition to load
   */
  getCompetitionByIdDetail(id: number): Observable<Competition> {
    console.log('Load competition details for ' + id);
    return this.httpClient.get<Competition>(this.competitionBaseUri + '/' + id + '/detail')
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
          for (const d of data) {
            d.dateOfBirth = new Date(d.dateOfBirth);
          }
          return data;
        })
      );
  }

  /**
   * Get participants of competition with details for managing.
   *
   * @param id id of competition to get participants for
   */
  getManagedParticipants(competitionId: number, filter?: PartFilterDto, page?: number, size?: number): Observable<Pageable<UserDetail>> {
    let params = {};
    if (filter) {
      params = {...params, ...filter};
    }
    if (page) {
      params = {...params, page};
    }
    if (size) {
      params = {...params, pageSize: size};
    }

    return this.httpClient
      .get<Pageable<UserDetail>>(`${this.competitionBaseUri}/${competitionId}/participants/registrations`, {params})
      .pipe(
        map((data: Pageable<UserDetail>) => {
          for (const d of data.content) {
            d.dateOfBirth = new Date(d.dateOfBirth);
          }
          return data;
        })
      );
  }

  getGroups(id: number): Observable<Array<SimpleGradingGroup>> {
    return this.httpClient
      .get<Array<SimpleGradingGroup>>(this.competitionBaseUri + '/' + id + '/groups');
  }

  getGroupsWithRegistrations(id: number): Observable<GradingGroupWithRegisterToDto[]> {
    return this.httpClient
      .get<GradingGroupWithRegisterToDto[]>(this.competitionBaseUri + '/' + id + '/group-registrations');
  }

  sendJudgingsForTournament(id: number, results: ParticipantResultDTO[]): Observable<ParticipantResult[]> {
    return this.httpClient.post<ParticipantResult[]>(this.competitionBaseUri + '/' + id + '/group-registrations', results);
  }

  updateRegisteredParticipants(competitionId: number, update: ParticipantManageDto[]): Observable<Array<ParticipantManageDto>>{
   return this.httpClient.patch<Array<ParticipantManageDto>>(`${this.competitionBaseUri}/${competitionId}/participants`, update);
  }

  /**
   * Searches competitions by searchParameters.
   *
   * @param searchParameters the parameters for the search
   */
  searchCompetitions(searchParameters: CompetitionSearchDto): Observable<SimpleCompetitionListDto> {
    let params = new HttpParams();
    Object.entries(searchParameters).forEach(
      ([k, v]) => {
        params = params.append(k, v);
      }
    );

    return this.httpClient
      .get<SimpleCompetitionListDto>(this.competitionBaseUri + '/search', {params})
        .pipe(
          map((data: SimpleCompetitionListDto) => {
            for(const d of data) {
              d.beginOfCompetition = new Date(d.beginOfCompetition);
              d.endOfCompetition = new Date(d.endOfCompetition);
              d.beginOfRegistration = new Date(d.beginOfRegistration);
              d.endOfRegistration = new Date(d.endOfRegistration);
            }
            return data;
          })
        );
  }
}
