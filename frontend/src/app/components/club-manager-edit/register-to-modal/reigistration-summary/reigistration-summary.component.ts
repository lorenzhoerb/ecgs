import {Component, OnInit, Input, EventEmitter, Output} from '@angular/core';
import {UserDetail, UserRegisterDetail} from '../../../../dtos/user-detail';
import {SimpleCompetitionListEntryDto} from '../../../../dtos/simpleCompetitionListEntryDto';
import {CompetitionService} from '../../../../services/competition.service';
import {SimpleGradingGroup} from '../../../../dtos/simple-grading-group';
import LocalizationService, {LocalizeService} from '../../../../services/localization/localization.service';

@Component({
  selector: 'app-reigistration-summary',
  templateUrl: './reigistration-summary.component.html',
  styleUrls: ['./reigistration-summary.component.scss']
})
export class ReigistrationSummaryComponent implements OnInit {

  @Input() competition: SimpleCompetitionListEntryDto;
  @Input()  participants: UserRegisterDetail[];

  @Output() groupChange = new EventEmitter<UserRegisterDetail[]>();

  gradingGroups: SimpleGradingGroup[];
  displayParticipants: UserDetail[];

  page = 1;
  pageSize = 5;

  constructor(private competitionService: CompetitionService) {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.refreshCountries();
    this.fetchGroups();
  }

  refreshCountries() {
    this.displayParticipants = this.participants.map((country, i) => ({ id: i + 1, ...country })).slice(
      (this.page - 1) * this.pageSize,
      (this.page - 1) * this.pageSize + this.pageSize,
    );
  }

  onSelectGroup() {
    this.refreshCountries();
    this.groupChange.emit(this.participants);
  }

  fetchGroups() {
    this.competitionService.getGroups(this.competition.id)
      .subscribe({
        next: data => this.gradingGroups = data,
        error: err => {
          console.error(err);
        }
      });
  }
}
