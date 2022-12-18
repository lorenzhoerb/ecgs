import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {map, Observable} from 'rxjs';
import {UserInfoDto} from '../dtos/userInfoDto';
import {Globals} from '../global/globals';
import {ResponseParticipantRegistrationDto} from '../dtos/responseParticipantRegistrationDto';
import {CompetitionDetail} from '../dtos/competition-detail';
import { ClubManagerTeamImportDto } from '../dtos/club-manager-team';
import { CalendarViewCompetition } from '../dtos/competition';
import { GeneralResponseDto } from '../dtos/general-response';
import {UserDetail} from '../dtos/user-detail';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userBaseUri: string = this.globals.backendUri + '/user';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  public getCompetitionsForCalender(year: number, weekNumber: number): Observable<CalendarViewCompetition[]> {
    return this.httpClient.get<CalendarViewCompetition[]>(`${this.userBaseUri}/calendar/${year}/${weekNumber}`);
  }

  public importTeam(clubManagerTeam: ClubManagerTeamImportDto): Observable<GeneralResponseDto> {
    return this.httpClient.post<GeneralResponseDto>(`${this.userBaseUri}/import-team`, clubManagerTeam);
  };


  getUserInfo(): Observable<UserInfoDto> {
    console.log('Load user info');
    return this.httpClient.get<UserInfoDto>(this.userBaseUri);
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
}
