import {HttpClient, HttpEvent, HttpParams, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {map, Observable, Subject} from 'rxjs';
import {UserInfoDto} from '../dtos/userInfoDto';
import {Globals} from '../global/globals';
import {ResponseParticipantRegistrationDto} from '../dtos/responseParticipantRegistrationDto';
import {ClubManagerTeamImportDto} from '../dtos/club-manager-team';
import {CalendarViewCompetition} from '../dtos/competition';
import {UserDetail} from '../dtos/user-detail';
import { ImportFlagsComponent } from '../components/import-flags/import-flags.component';
import {ImportFlagsResultDto} from '../dtos/import-flags-result-dto';
import { ImportFlag } from '../dtos/import-flag';
import {SimpleFlagDto} from '../dtos/simpleFlagDto';
import {UserDetailSetFlagDto} from '../dtos/userDetailSetFlagDto';
import {ClubManagerTeamImportResults} from '../dtos/club-manager-team-import-results';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  public userInfoDto$: Subject<UserInfoDto>;

  private userBaseUri: string = this.globals.backendUri + '/user';

  constructor(private httpClient: HttpClient, private globals: Globals) {
    this.userInfoDto$ = new Subject<UserInfoDto>();
  }

  public getCompetitionsForCalender(year: number, weekNumber: number): Observable<CalendarViewCompetition[]> {
    const params = new HttpParams()
      .set('year', year)
      .set('weekNumber', weekNumber);

    return this.httpClient.get<CalendarViewCompetition[]>(`${this.userBaseUri}/calendar`, {params});
  }

  public importTeam(clubManagerTeam: ClubManagerTeamImportDto): Observable<ClubManagerTeamImportResults> {
    return this.httpClient.post<ClubManagerTeamImportResults>(`${this.userBaseUri}/import-team`, clubManagerTeam);
  };

  public importFlags(flags: ImportFlag[]): Observable<ImportFlagsResultDto> {
    return this.httpClient.post<ImportFlagsResultDto>(`${this.userBaseUri}/flags`, flags);
  }

  updateUserInfo(): void {
    this.httpClient.get<UserInfoDto>(this.userBaseUri).subscribe(value => {
      if (value.picturePath != null)  {
        value.picturePath = 'http://localhost:8080/' + value.picturePath + '?' + new Date().getTime();
      }
      this.userInfoDto$.next(value);
    });
  }

  getUserDetail(id?: number): Observable<UserDetail> {
    console.log('Load user info');
    return id === null
      ? this.httpClient.get<UserDetail>(`${this.userBaseUri}/detail`)
      : this.httpClient.get<UserDetail>(`${this.userBaseUri}/${id}`);
  }

  registerToCompetition(competitionId: number, groupPreference: number): Observable<ResponseParticipantRegistrationDto> {
    const uri = this.userBaseUri + '/competitions/' + competitionId;
    if (groupPreference) {
      return this.httpClient
        .post<ResponseParticipantRegistrationDto>(uri, {groupPreference});
    }
    return this.httpClient
      .post<ResponseParticipantRegistrationDto>(uri, null);
  }

  isRegisteredToCompetition(competitionId: number): Observable<void> {
    return this.httpClient
      .get<void>(this.userBaseUri + '/competitions/' + competitionId);
  }

  searchByName(name: string, max: number): Observable<UserDetail[]> {
    console.log('search users by name');
    const params = new HttpParams()
      .set('name', name)
      .set('max', max);
    return this.httpClient.get<UserDetail[]>(this.userBaseUri + '/search', {params})
      .pipe(
        map((data: UserDetail[]) => data.map((user: UserDetail) => {
          user.dateOfBirth = new Date(user.dateOfBirth);
          return user;
        })));
  }

  uploadPicture(file: File): Observable<HttpEvent<any>> {
    const multipartFile: FormData = new FormData();
    multipartFile.append('file', file, file.name);
    const request = new HttpRequest('POST', this.userBaseUri + '/picture', multipartFile, {
      responseType: 'text'
    });
    return this.httpClient.request(request);
  }

  getPicture(): Observable<any> {
    return this.httpClient.get(this.userBaseUri + '/picture');
  }

  getManagedFlags(): Observable<SimpleFlagDto[]> {
    return this.httpClient.get<SimpleFlagDto[]>(this.userBaseUri + '/my-flags');
  }

  addMemberFlags(members: UserDetailSetFlagDto): Observable<void> {
    return this.httpClient.post<void>(this.userBaseUri + '/members/flags', members);
  }

  removeMemberFlags(members: UserDetailSetFlagDto): Observable<void> {
    return this.httpClient.patch<void>(this.userBaseUri + '/members/flags', members);
  }

  getMembers(): Observable<Array<UserDetail>> {
    return this.httpClient
      .get<Array<UserDetail>>(this.userBaseUri + '/members')
      .pipe(
        map((data: Array<UserDetail>) => {
          for (const d of data) {
            d.dateOfBirth = new Date(d.dateOfBirth);
          }
          return data;
        })
      );
  }
}
