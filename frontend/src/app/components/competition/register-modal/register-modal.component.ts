import {Component, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Competition} from '../../../dtos/competition';
import {SimpleGradingGroup} from '../../../dtos/simple-grading-group';
import {CompetitionService} from '../../../services/competition.service';
import {UserService} from '../../../services/user.service';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, {LocalizeService} from '../../../services/localization/localization.service';

@Component({
  selector: 'app-register-modal',
  templateUrl: './register-modal.component.html',
  styleUrls: ['./register-modal.component.scss']
})
export class RegisterModalComponent implements OnInit {

  competition: Competition;
  competitionId: number;
  groups: Array<SimpleGradingGroup>;
  groupPreference = null;

  constructor(
    public activeModal: NgbActiveModal,
    private competitionService: CompetitionService,
    private userService: UserService,
    private toastrService: ToastrService
  ) {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.competitionService.getGroups(this.competitionId).subscribe({
      next: data => {
        this.groups = data;
      },
      error: err => console.log(err)
    });
  }

  getSelectedGroup(): SimpleGradingGroup {
    if (this.groupPreference === null) {
      return null;
    }
    return this.groups.filter(g => g.id === this.groupPreference)[0];
  }


  formatDate(date: Date): string {
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }

  onSubmit(): void {
    this.userService.registerToCompetition(this.competitionId, this.groupPreference === -1 ? null : this.groupPreference)
      .subscribe({
        next: value => {
          this.toastrService.success(this.localize.succRegComp);
          this.activeModal.close(true);
        },
        error: err => {
          err.error.errors.forEach(e => {
            this.toastrService.error(e);
          });
          this.toastrService.error(this.localize.errRegComp);
          this.activeModal.close(false);
        }
      });
  }
}
