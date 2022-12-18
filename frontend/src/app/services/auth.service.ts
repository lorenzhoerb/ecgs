import {Injectable} from '@angular/core';
import {AuthRequest, RegisterRequest} from '../dtos/auth-request';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
// @ts-ignore
import jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import {UserPasswordReset} from '../dtos/userPasswordReset';
import {UserCredentialUpdate} from '../dtos/userCredentialUpdate';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authBaseUri: string = this.globals.backendUri + '/authentication';
  private registerUri: string = this.globals.backendUri + '/registration';
  private forgotUri: string = this.globals.backendUri + '/forgot';
  private resetUri: string = this.globals.backendUri + '/reset';
  private changeUri: string = this.globals.backendUri + '/changePassword';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals) {

  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   *
   * @param authRequest User data
   */
  loginUser(authRequest: AuthRequest): Observable<string> {
    return this.httpClient.post(this.authBaseUri, authRequest, {
      responseType: 'text'
    })
      .pipe(
        tap((authResponse: string) => this.setToken(authResponse))
      );
  }

  /**
   * Registers a user.
   *
   * @param registerRequest the given data to register
   */
  registerUser(registerRequest: RegisterRequest): Observable<string> {
    return this.httpClient.post(this.registerUri, registerRequest, { responseType: 'text'})
      .pipe();
  }

  requestPasswordReset(email: string): Observable<string> {
    return this.httpClient.post<string>(this.forgotUri, {email})
      .pipe();
  }

  resetPassword(userPasswordReset: UserPasswordReset): Observable<string> {
    return this.httpClient.post<string>(this.resetUri, userPasswordReset)
      .pipe();
  }

  changePassword(userCredentialUpdate: UserCredentialUpdate): Observable<string> {
    return this.httpClient.post<string>(this.changeUri, userCredentialUpdate)
      .pipe();
  }

  /**
   * Check if a valid JWT token is saved in the localStorage
   */
   isLoggedIn() {
    return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
  }

  logoutUser() {
    console.log('Logout');
    localStorage.removeItem('authToken');
  }

  getToken() {
    return localStorage.getItem('authToken');
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole() {
    if (this.getToken() != null) {
      const decoded: any = jwt_decode(this.getToken());
      const authInfo: string[] = decoded.rol;
      if (authInfo.includes('ROLE_ADMIN')) {
        return 'ADMIN';
      } else if (authInfo.includes('ROLE_USER')) {
        return 'USER';
      }
    }
    return 'UNDEFINED';
  }

  getUserName() {
    if (this.getToken() != null) {
      const decoded: any = jwt_decode(this.getToken());
      const authInfo: string = decoded.sub;
      return authInfo;
    }
  }

  private setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }

  private getTokenExpirationDate(token: string): Date {

    const decoded: any = jwt_decode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

}
