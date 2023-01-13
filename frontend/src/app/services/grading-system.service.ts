import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class GradingSystemService {

  private gradingSystemBaseUri: string = this.globals.backendUri + '/grading-systems';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /*
   * Create a gradingSystem. Use only for creating templates!!!
   *
   * @param gradingSystem gradingSystem to create
   */
  createGradingSystem(gradingSystem: any): Observable<any> {
    return this.httpClient
      .post<any>(this.gradingSystemBaseUri, gradingSystem);
  }
}
