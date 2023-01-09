import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {CompetitionSearchDto} from 'src/app/dtos/competitionSearchDto';
import {SimpleCompetitionListDto} from 'src/app/dtos/simpleCompetitionListDto';
import {CompetitionService} from 'src/app/services/competition.service';

@Component({
  selector: 'app-competition-list-view',
  templateUrl: './competition-list-view.component.html',
  styleUrls: ['./competition-list-view.component.scss']
})
export class CompetitionListViewComponent implements OnInit {
  competitions: SimpleCompetitionListDto = [];

  constructor(
    private competitionService: CompetitionService,
    private notification: ToastrService
  ) { }

  ngOnInit(): void {
    // filter component raises an event, that calls fetchCompetition()
  }

  fetchCompetitions(competitionSearch: CompetitionSearchDto): void {
    this.competitionService.searchCompetitions(competitionSearch).subscribe(
      {
        next: (data: SimpleCompetitionListDto) => {
          this.competitions = data;
        },
        error: (err) => {
          console.log(err);
          if (err.status === 401 || err.status === 403) {
            this.notification.error('Unauthenticated!', 'Error');
            return;
          }
          this.notification.error(err.message, 'Error');
        }
      }
    );
  }

  competitionSearchChange(newCompetitionSearch: CompetitionSearchDto): void {
    this.fetchCompetitions(newCompetitionSearch);
  }
}
