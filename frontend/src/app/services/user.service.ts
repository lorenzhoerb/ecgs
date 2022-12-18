import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ClubManagerTeamImportDto } from '../dtos/club-manager-team';
import { CalendarViewCompetition } from '../dtos/competition';
import { GeneralResponseDto } from '../dtos/general-response';
import { UserInfoDto } from '../dtos/userInfoDto';
import { Globals } from '../global/globals';

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
}
