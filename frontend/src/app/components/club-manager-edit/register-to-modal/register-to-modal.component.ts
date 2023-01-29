import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {SimpleCompetitionListEntryDto} from '../../../dtos/simpleCompetitionListEntryDto';
import {UserRegisterDetail} from '../../../dtos/user-detail';
import {ParticipantRegistrationDto} from '../../../dtos/ParticipantRegistrationDto';
import {CompetitionService} from '../../../services/competition.service';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, {LocalizeService} from '../../../services/localization/localization.service';

enum RegisterState {
  selectCompetition,
  summary
}

@Component({
  selector: 'app-register-to-modal',
  templateUrl: './register-to-modal.component.html',
  styleUrls: ['./register-to-modal.component.scss']
})
export class RegisterToModalComponent implements OnInit {

  @Input() participants: UserRegisterDetail[];

  registerState = RegisterState;
  state: RegisterState = RegisterState.selectCompetition;
  selectedComp: SimpleCompetitionListEntryDto;
  canRegister = false;

  constructor(
    public activeModal: NgbActiveModal,
    private competitionService: CompetitionService,
    private toastr: ToastrService
  ) {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }

  onCompSelect(comp: SimpleCompetitionListEntryDto) {
    this.selectedComp = comp;
    this.state = RegisterState.summary;
  }

  onBack() {
    this.selectedComp = null;
    this.state = RegisterState.selectCompetition;
  }

  onRegister() {
    this.competitionService
      .registerParticipants(this.selectedComp.id, this.mapMultiRegister(this.participants))
      .subscribe({
        next: data => {
          this.toastr.success(`Successfully registered ${data.registeredParticipants.length} members`);
          this.activeModal.close();
        },
        error: err => {
          this.displayRegistrationErrors(err.error.errors);
          this.toastr.error('Registration cancelled.');
          this.activeModal.close();
        }
      });
  }

  displayRegistrationErrors(bulkErrors) {
    bulkErrors.forEach(bulkError => {
      const participant = this.participants.filter(p => bulkError.id === p.id)[0];
      let errMessage = `Registration failed for ${participant.firstName} ${participant.lastName}:\n`;
      bulkError.errors.forEach(userBulkError => {
        errMessage += '- ' + userBulkError + '\n';
      });
      this.toastr.error(errMessage);
    });
  }


  mapMultiRegister(participants: UserRegisterDetail[]): ParticipantRegistrationDto[] {
    return participants.map(p => ({
      userId: p.id,
      groupPreference: p.groupId
    }));
  }

  onGroupChange(participants: UserRegisterDetail[]) {
    this.participants = participants;
    const notSelected = participants.filter(p => p.groupId == null).length;
    this.canRegister = notSelected === 0;
  }
}
