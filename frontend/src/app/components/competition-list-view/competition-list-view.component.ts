import { HttpClient } from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {CompetitionSearchDto} from 'src/app/dtos/competitionSearchDto';
import {SimpleCompetitionListDto} from 'src/app/dtos/simpleCompetitionListDto';
import {CompetitionService} from 'src/app/services/competition.service';
import { GradeService } from 'src/app/services/grade.service';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-competition-list-view',
  templateUrl: './competition-list-view.component.html',
  styleUrls: ['./competition-list-view.component.scss']
})
export class CompetitionListViewComponent implements OnInit {
  competitions: SimpleCompetitionListDto = [];
  competitionsPerPage = 5;
  currentPage = 1;

  constructor(
    private competitionService: CompetitionService,
    private notification: ToastrService,
    private http: HttpClient,
  ) { }


  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    // filter component raises an event, that calls fetchCompetition()
  }

  public onPageChangeClick(change: number) {
    if (change > 0 && Math.ceil(this.competitions.length / this.competitionsPerPage) - this.currentPage <= 0) {
      return;
    }
    if (change < 0 && this.currentPage + change <= 0) {
      return;
    }
    this.currentPage += change;
  }


  public fetchCompetitions(competitionSearch: CompetitionSearchDto): void {
    this.competitionService.searchCompetitions(competitionSearch).subscribe(
      {
        next: (data: SimpleCompetitionListDto) => {
          this.currentPage = 1;
          this.competitions = data;
        },
        error: (err) => {
          if (err.status === 0) {
            this.notification.error('Could not reach server', 'Connection error');
          } else if (err.status === 401 || err.status === 403) {
            this.notification.error('Unauthenticated!', 'Error');
          } else {
            this.notification.error(err.message, 'Error');
          }
        }
      }
    );
  }

  public getCompetitionsForCurrentPage(): SimpleCompetitionListDto {
    const lowerBound = (this.currentPage - 1) * this.competitionsPerPage;
    const upperBound = lowerBound + this.competitionsPerPage;

    return this.competitions.slice(lowerBound, upperBound);
  }

  public getTotalPagesAvailable(): number {
    return Math.max(Math.ceil(this.competitions.length / 5), 1);
  }

  competitionSearchChange(newCompetitionSearch: CompetitionSearchDto): void {
    this.fetchCompetitions(newCompetitionSearch);
  }
}
