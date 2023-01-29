import {Injectable} from '@angular/core';
import {map, Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {RegisterConstraint} from '../dtos/register-constraint';
import {DetailedGradingGroupDto} from '../dtos/detailed-grading-group-dto';
import {Pageable} from '../dtos/pageable';
import {UserDetail, UserDetailGrade} from '../dtos/user-detail';

@Injectable({
  providedIn: 'root'
})
export class GradingGroupService {

  private gradingGroupBaseUri: string = this.globals.backendUri + '/groups';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  setGradingGroupConstraints(gradingGroupId: number, constraints: RegisterConstraint[]): Observable<RegisterConstraint[]> {
    return this
      .httpClient.post<RegisterConstraint[]>(`${this.gradingGroupBaseUri}/${gradingGroupId}/constraints`, constraints);
  }

  getOneById(gradingGroupId: number): Observable<DetailedGradingGroupDto> {
    return this.httpClient
      .get<DetailedGradingGroupDto>(`${this.gradingGroupBaseUri}/${gradingGroupId}`);
  }

  /**
   * Get aprticipants of grading group.
   *
   * @param id id of grading group to get participants for
   * @param filter filter specifying what to filter by
   */
  getParticipants(id: number, filter?: any, page?: number, size?: number): Observable<Pageable<UserDetailGrade>> {
    filter.page = page;
    filter.size = size;

    return this.httpClient
      .get<Pageable<UserDetailGrade>>(this.gradingGroupBaseUri + '/' + id + '/participants', {params: {...filter}})
      .pipe(
        map((data: Pageable<UserDetailGrade>) => {
          for (const d of data.content) {
            d.dateOfBirth = new Date(d.dateOfBirth);
          }
          return data;
        })
      );
  }
}
