import {Component, OnInit} from '@angular/core';
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
import {UserDetailFilterDto} from '../../dtos/userDetailFilterDto';
import {debounceTime, Subject} from 'rxjs';
import {Router} from '@angular/router';
import {ParticipantManageDto} from '../../dtos/participant-manage-dto';

@Component({
  selector: 'app-club-manager-edit',
  templateUrl: './club-manager-edit.component.html',
  styleUrls: ['./club-manager-edit.component.scss']
})
export class ClubManagerEditComponent implements OnInit {
  currentLanguage = SupportedLanguages.German;
  page = 1;
  pageSize = 10;
  totalElements = 10;
  participants: UserDetail[] = [];
  searchParameters: UserDetailFilterDto = {};
  inputChange: Subject<any> = new Subject();
  bulkMap: Map<number, UserDetail> = new Map<number, UserDetail>();
  masterBulked = false;
  bulkType = 0;

  canEditParticipants = false;
  myFlags: Map<number, SimpleFlagDto> = new Map();
  newFlagText = '';
  selectedFlag: string;
  newId = -1;

  constructor(private userService: UserService,
              private authService: AuthService,
              private router: Router,
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

    this.inputChange
      .pipe(debounceTime(200))
      .subscribe(e => {
        this.page = 1;
        this.getMembers();
      });

    this.getMembers();
  }

  getMembers() {
    this.userService
      .getMembers(
        this.searchParameters,
        this.page - 1,
        this.pageSize).subscribe({
      next: data => {
        this.participants = data.content;
        this.pageSize = data.size;
        this.totalElements = data.totalElements;
        this.page = data.pageable.pageNumber + 1;
      },
      error: err => {
        this.router.navigate(['/']);
      }
    });
  }

  bulkAction(action) {
    this.masterBulked = false;
    action(Array.from(this.bulkMap.values()));
    this.bulkMap.clear();
  }

  addFlags() {
    const flag = this.myFlags.get(parseInt(this.selectedFlag, 10));
    const users = Array.from(this.bulkMap.values());
    this.userService.addMemberFlags({flag, users}).subscribe({
      next: data => {
        this.getMembers();
        this.getManagedFlags();
      },
      error: error => {
        this.toastr.error(error, 'Error setting flags');
        console.log(error);
      }
    });
  }

  registerTo() {
    const users = Array.from(this.bulkMap.values());
    if (!users || users.length === 0) {
      this.toastr.error('No members selected to register.');
      return;
    }
    const modalRef = this.modalService.open(RegisterToModalComponent, {size: 'lg'});
    modalRef.componentInstance.participants = users.map(u => ({...u, groupId: null}));
  }

  removeFlags() {
    const flag = this.myFlags.get(parseInt(this.selectedFlag, 10));
    const users = Array.from(this.bulkMap.values());
    this.userService.removeMemberFlags({flag, users}).subscribe({
      next: data => {
        this.getMembers();
        this.getManagedFlags();
      },
      error: error => {
        this.toastr.error(error, 'Error setting flags');
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

  onBulk(bulked: boolean, record: UserDetail) {
    if (bulked) {
      this.bulkMap.set(record.id, this.deepCopy(record));
    } else {
      this.bulkMap.delete(record.id);
    }
  }

  onMasterBulk(bulked: boolean) {
    this.participants.forEach(p => {
      this.onBulk(bulked, p);
    });
  }

  onPageChange() {
    this.getMembers();
    this.masterBulked = false;
  }

  currentFlags(): SimpleFlagDto[] {
    return Array.from(this.myFlags.values());
  }

  mapFlags(flags: SimpleFlagDto[]): string {
    if (flags == null) {
      return '';
    }

    return flags.map(f => f.name).join(', ');
  }

  mapFlagsLengthChecked(flags: SimpleFlagDto[]): string {
    const result = this.mapFlags(flags);

    if (result.length > 10) {
      return result.substr(0, 10) + '...';
    } else {
      return result;
    }
  }

  deepCopy(x: any): any {
    return JSON.parse(JSON.stringify(x));
  }

  onAddRemoveFlags(modalContent) {
    this.modalService.open(modalContent, {ariaLabelledBy: 'modal-basic-title'}).result.then(
      result => {
        if (result === 'add') {
          this.addFlags();
        } else if (result === 'remove') {
          this.removeFlags();
        }
      }
    );
  }
}
