import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import { ViewEditGradingGroup, SimpleGradingGroupViewEdit, ViewEditGradingGroupSearch } from '../dtos/grading-group-detail';

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

  public getDraftGradingSystemById(id: number): Observable<ViewEditGradingGroup> {
    return this.httpClient.get<ViewEditGradingGroup>(`${this.gradingSystemBaseUri}/drafts/${id}`);
  }

  public getSimpleDraftGradingSystems(): Observable<SimpleGradingGroupViewEdit[]> {
    return this.httpClient.get<SimpleGradingGroupViewEdit[]>(`${this.gradingSystemBaseUri}/drafts/simple`);
  }

  public updateGradingSystem(gradingSystem: any): Observable<any> {
    return this.httpClient.put<any>(`${this.gradingSystemBaseUri}/drafts`, gradingSystem);
  }

  public deleteGradingSystem(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.gradingSystemBaseUri}/drafts/${id}`);
  }
}
