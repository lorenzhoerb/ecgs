import {Component, OnInit} from '@angular/core';
import {ContentCardComponent} from '../content-card/content-card.component';
import {CompetitionService} from '../../services/competition.service';
import {Competition} from '../../dtos/competition';
import {ActivatedRoute} from '@angular/router';
import {SupportedLanguages} from '../../services/localization/language';
import LocalizationService, {LocalizeService} from '../../services/localization/localization.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {RegisterModalComponent} from './register-modal/register-modal.component';
import {UserService} from '../../services/user.service';
import {cloneDeep} from 'lodash';
import {UserDetail} from '../../dtos/user-detail';
import { SimpleGradingGroup } from 'src/app/dtos/simple-grading-group';

@Component({
  selector: 'app-competition-view',
  templateUrl: './competition.component.html',
  styleUrls: ['./competition.component.scss']
})
export class CompetitionComponent implements OnInit {
  id: number;
  competition: Competition = null;
  error: Error = null;
  currentLanguage = SupportedLanguages.English;
  isRegisteredToCompetition = false;
  canRegister = false;
  participants: UserDetail[];
  groups: SimpleGradingGroup[];

  constructor(private service: CompetitionService,
              private route: ActivatedRoute,
              private modalService: NgbModal,
              private userService: UserService) {
    console.log('init');
    this.localize.changeLanguage(this.currentLanguage);
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.id = parseInt(params.id, 10);

        this.service.getCompetitionById(this.id).subscribe({
          next: data => {
            this.competition = data;
            this.error = null;
            this.service.getGroups(this.id).subscribe({
              next: data2 => {
                this.groups = data2;
                this.initCanRegister();
              },
              error: err => console.log(err)
            });
            this.fetchIsRegistered(this.id);
            this.fetchParticipants();
          },
          error: error => {
            console.error('Error fetching competition information', error);
            this.error = error;
            this.competition = null;
          }
        });
      }
    });
  }

  fetchIsRegistered(id) {
    this.userService.isRegisteredToCompetition(id)
      .subscribe(value => this.isRegisteredToCompetition = true);
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }

  toggleLanguage() {
    if (this.currentLanguage === SupportedLanguages.English) {
      this.currentLanguage = SupportedLanguages.German;
    } else {
      this.currentLanguage = SupportedLanguages.English;
    }
    this.localize.changeLanguage(this.currentLanguage);
  }

  initCanRegister() {
    const now = new Date();
    this.canRegister = now >= this.competition.beginOfRegistration && now <= this.competition.endOfRegistration;
    this.canRegister = this.canRegister && this.groups.length > 0;
    console.log(this.canRegister);
  }

  pictureEmpty() {
    return this.competition.picturePath === '' || this.competition.picturePath === null;
  }

  onRegister() {
    const modalRef = this.modalService.open(RegisterModalComponent);
    modalRef.componentInstance.competition = this.competition;
    modalRef.componentInstance.competitionId = this.id;
    modalRef.closed.subscribe(registered => {
      if (registered) {
        this.isRegisteredToCompetition = true;
        this.fetchParticipants();
      }
    });
  }


  fetchParticipants() {
    this.service.getParticipants(this.id).subscribe({
      next: data => {
        this.participants = data;
        console.log(data);
        this.error = null;
      },
      error: error => {
        this.participants = [];
        console.error('Error fetching competition information', error);
        this.error = error;
      }
    });
  }
}
