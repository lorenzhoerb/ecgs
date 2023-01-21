import { Component, OnInit } from '@angular/core';
import {genderMap, UserDetail} from '../../dtos/user-detail';
import {SimpleFlagDto} from '../../dtos/simpleFlagDto';
import {CompetitionService} from '../../services/competition.service';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, {LocalizeService} from '../../services/localization/localization.service';
import {SupportedLanguages} from '../../services/localization/language';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {RegisterToModalComponent} from './register-to-modal/register-to-modal.component';

@Component({
  selector: 'app-club-manager-edit',
  templateUrl: './club-manager-edit.component.html',
  styleUrls: ['./club-manager-edit.component.scss']
})
export class ClubManagerEditComponent implements OnInit {
  currentLanguage = SupportedLanguages.German;
  updateCounter = 0;
  canEditParticipants = false;
  myFlags: Map<number, SimpleFlagDto> = new Map();
  newFlagText = '';
  selectedFlag: string;
  newId = -1;

  constructor(private userService: UserService,
              private authService: AuthService,
              private toastr: ToastrService,
              private modalService: NgbModal) {
    this.localize.changeLanguage(this.currentLanguage);
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    const role = this.authService.getUserRole();

    if (role === 'TOURNAMENT_MANAGER' || role === 'CLUB_MANAGER') {
      this.canEditParticipants = true;
      this.getManagedFlags();
    }
  }

  fetchParticipantsWithFlags = () => this.userService.getMembers();

  bulkAction = (users: any[]) => {
  };

  addFlag() {
    this.bulkAction = (users => {
      this.addFlags(this.deepCopy(users));
    });
  }

  deleteFlag() {
    this.bulkAction = (users => {
      this.removeFlags(this.deepCopy(users));
    });
  }

  addFlags(users: UserDetail[]) {
    const flag = this.myFlags.get(parseInt(this.selectedFlag, 10));
    this.userService.addMemberFlags({flag, users}).subscribe({
      next: data => {
        this.updateCounter++;
        this.getManagedFlags();
      },
      error: error => {
        this.toastr.error(error, 'Error setting flags');
        this.updateCounter++;
      }
    });
  }

  registerTo() {
    this.bulkAction = (users => {
      if(!users || users.length === 0) {
        this.toastr.error('No members selected to register.');
        return;
      }
      const modalRef = this.modalService.open(RegisterToModalComponent, {size: 'lg'});
      modalRef.componentInstance.participants = users.map(u => ({...u, groupId: null}));
      modalRef.closed.subscribe(registered => {

      });
    });
  }

  removeFlags(users: UserDetail[]) {
    const flag = this.myFlags.get(parseInt(this.selectedFlag, 10));
    this.userService.removeMemberFlags({flag, users}).subscribe({
      next: data => {
        this.updateCounter++;
        this.getManagedFlags();
      },
      error: error => {
        this.toastr.error(error, 'Error setting flags');
        this.updateCounter++;
      }
    });
  }

  getManagedFlags() {
    this.userService.getManagedFlags().subscribe({
      next: data => {
        this.myFlags.clear();

        for (const flag of data) {
          this.myFlags.set(flag.id, flag);
        }

        if (data.length !== 0) {
          this.selectedFlag = String(data[0].id);
        }
      },
      error: error => {
        this.toastr.error(error, 'Error fetching information');
      }
    });
  }

  newFlag() {
    if (this.myFlags.size !== 0 &&
      Array.from(this.myFlags.values()).filter(f => f.name === this.newFlagText).length !== 0) {
      this.toastr.error('flag invalid', 'flag already exists');
      return;
    }
    this.myFlags.set(this.newId, {id: this.newId, name: this.newFlagText});
    this.selectedFlag = String(this.newId);
    this.newFlagText = '';
    --this.newId;
  }

  mapParticipantWithFlags(p: UserDetail): any[] {
    return [p.firstName, p.lastName, genderMap.get(p.gender), p.dateOfBirth.toLocaleDateString(),
      p.flags.map(f => f.name).join(', ')];
  }

  currentFlags(): SimpleFlagDto[] {
    return Array.from(this.myFlags.values());
  }

  deepCopy(x: any) {
    return JSON.parse(JSON.stringify(x));
  }
}
