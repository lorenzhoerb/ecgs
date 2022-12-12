import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { UserInfoDto } from '../dtos/userInfoDto';
import { Globals } from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/user';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getUserInfo(): Observable<UserInfoDto> {
    console.log('Load user info');
    return this.httpClient.get<UserInfoDto>(this.userBaseUri);
  }
}
