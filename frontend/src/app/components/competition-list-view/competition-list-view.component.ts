import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {CompetitionSearchDto} from 'src/app/dtos/competitionSearchDto';
import {SimpleCompetitionListDto} from 'src/app/dtos/simpleCompetitionListDto';
import {CompetitionService} from 'src/app/services/competition.service';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-competition-list-view',
  templateUrl: './competition-list-view.component.html',
  styleUrls: ['./competition-list-view.component.scss']
})
export class CompetitionListViewComponent implements OnInit {
  competitions: SimpleCompetitionListDto = [];
  competitionPerPage = 5;
  currentPage = 1;

  constructor(
    private competitionService: CompetitionService,
    private notification: ToastrService
  ) { }


  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    // filter component raises an event, that calls fetchCompetition()
  }

  public onPageChangeClick(change: number) {
    if (change > 0 && Math.ceil(this.competitions.length / this.competitionPerPage) - this.currentPage <= 0) {
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
    const lowerBound = (this.currentPage - 1) * this.competitionPerPage;
    const upperBound = lowerBound + this.competitionPerPage;

    return this.competitions.slice(lowerBound, upperBound);
  }

  competitionSearchChange(newCompetitionSearch: CompetitionSearchDto): void {
    this.fetchCompetitions(newCompetitionSearch);
  }
}
