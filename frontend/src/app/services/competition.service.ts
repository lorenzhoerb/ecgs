import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent, HttpParams, HttpRequest} from '@angular/common/http';
import {UserDetail} from '../dtos/user-detail';
import {Competition} from '../dtos/competition';
import {CompetitionDetail} from '../dtos/competition-detail';
import {map, Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {SimpleGradingGroup} from '../dtos/simple-grading-group';

import {CompetitionSearchDto} from '../dtos/competitionSearchDto';
import {ParticipantRegistrationDto, ResponseParticipantRegistrationDto} from '../dtos/ParticipantRegistrationDto';
import {AdvanceCompetitionSearchDto} from '../dtos/advance-competition-searchDto';
import {SimpleCompetitionListEntryDto} from '../dtos/simpleCompetitionListEntryDto';
import {ParticipantResult, ParticipantResultDTO, SimpleCompetitionListDto} from '../dtos/simpleCompetitionListDto';
import {GradingGroupWithRegisterToDto} from '../dtos/gradingGroupWithRegisterToDto';
import {PartFilterDto} from '../dtos/part-filter-dto';
import {ParticipantManageDto} from '../dtos/participant-manage-dto';
import {Pageable} from '../dtos/pageable';
import { DownloadReportRequestDto } from '../dtos/excel-download-request-dto';
import { DownloadReportResponseDto } from '../dtos/excel-download-responce-dto';
import { ReportDownloadOptions } from '../dtos/report-download-options';
import { ReportIsDownloadable } from '../dtos/report-is-downloadable';
import {UserDetailFilterDto} from '../dtos/userDetailFilterDto';
import {SimpleFlagDto} from '../dtos/simpleFlagDto';
import {UserDetailSetFlagDto} from '../dtos/userDetailSetFlagDto';

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

  searchCompetitionsAdvance(searchParameters: AdvanceCompetitionSearchDto): Observable<Pageable<SimpleCompetitionListEntryDto>> {
    return this.httpClient
      .get<Pageable<SimpleCompetitionListEntryDto>>(this.competitionBaseUri, {params: {...searchParameters}})
      .pipe(
        map((data: Pageable<SimpleCompetitionListEntryDto>) => {
          for (const d of data.content) {
            d.beginOfCompetition = new Date(d.beginOfCompetition);
            d.endOfCompetition = new Date(d.endOfCompetition);
            d.beginOfRegistration = new Date(d.beginOfRegistration);
            d.endOfRegistration = new Date(d.endOfRegistration);
          }
          return data;
        })
      );
  }

  registerParticipants(competitionId: number, registrations: ParticipantRegistrationDto[]): Observable<ResponseParticipantRegistrationDto> {
    return this.httpClient
      .post<ResponseParticipantRegistrationDto>(`${this.competitionBaseUri}/${competitionId}/participants`, registrations);
  }

  /**
   * Get aprticipants of competition.
   *
   * @param id id of competition to get participants for
   * @param filter filter specifying what to filter by
   */
  getParticipants(id: number, filter?: any, page?: number, size?: number): Observable<Pageable<UserDetail>> {
    filter.page = page;
    filter.size = size;

    return this.httpClient
      .get<Pageable<UserDetail>>(this.competitionBaseUri + '/' + id + '/participants', {params: {...filter}})
      .pipe(
        map((data: Pageable<UserDetail>) => {
          for (const d of data.content) {
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
    if (filter.flagId != null && (filter.flagId as any) !== '') {
      filter.flagId = parseInt((filter.flagId as any), 10);
    }
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

  updateRegisteredParticipants(competitionId: number, update: ParticipantManageDto[]): Observable<Array<ParticipantManageDto>> {
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

          for (const d of data) {
            d.beginOfCompetition = new Date(d.beginOfCompetition);
            d.endOfCompetition = new Date(d.endOfCompetition);
            d.beginOfRegistration = new Date(d.beginOfRegistration);
            d.endOfRegistration = new Date(d.endOfRegistration);
          }
          return data;
        })
      );
  }

  calculateReportResults(competitionId: number) {
    return this.httpClient.post(`${this.competitionBaseUri}/${competitionId}/report`, {});
  }

  downloadReport(competitionId: number, requestDto: DownloadReportRequestDto): Observable<DownloadReportResponseDto> {
    return this.httpClient.post<DownloadReportResponseDto>(`${this.competitionBaseUri}/${competitionId}/report/download`, requestDto);
  }

  getCurrentUserReportDownloadOptions(competitionId: number): Observable<ReportDownloadOptions> {
    return this.httpClient.get<ReportDownloadOptions>(`${this.competitionBaseUri}/${competitionId}/report/download-inclusion-rule-options`);
  }

  checkIfReportsAreDownloadReady(competitionId: number): Observable<ReportIsDownloadable> {
    return this.httpClient.get<ReportIsDownloadable>(`${this.competitionBaseUri}/${competitionId}/report/downloadable`);
  }

  uploadPicture(id: number, file: File): Observable<HttpEvent<any>> {
    const multipartFile: FormData = new FormData();
    multipartFile.append('file', file, file.name);
    const request = new HttpRequest('POST', this.competitionBaseUri + '/' + id + '/picture', multipartFile, {
      responseType: 'text'
    });
    return this.httpClient.request(request);
  }

  getManagedFlags(id: number): Observable<SimpleFlagDto[]> {
    return this.httpClient.get<SimpleFlagDto[]>(this.competitionBaseUri + '/' + id + '/my-flags');
  }

  addMemberFlags(id: number, members: UserDetailSetFlagDto): Observable<void> {
    return this.httpClient.post<void>(this.competitionBaseUri + '/' + id + '/members/flags', members);
  }

  removeMemberFlags(id: number, members: UserDetailSetFlagDto): Observable<void> {
    return this.httpClient.patch<void>(this.competitionBaseUri + '/' + id + '/members/flags', members);
  }
}
