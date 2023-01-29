import {Component, OnInit} from '@angular/core';
import {CompetitionService} from '../../../services/competition.service';
import {ActivatedRoute, Router} from '@angular/router';
import {debounceTime, Subject} from 'rxjs';
import {UserDetail} from '../../../dtos/user-detail';
import {SimpleGradingGroup} from '../../../dtos/simple-grading-group';
import {PartFilterDto} from '../../../dtos/part-filter-dto';
import {ParticipantManageDto} from '../../../dtos/participant-manage-dto';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import LocalizationService, {LocalizeService} from '../../../services/localization/localization.service';
import {ToastrService} from 'ngx-toastr';
import {SimpleFlagDto} from '../../../dtos/simpleFlagDto';
import {RegisterToModalComponent} from '../../club-manager-edit/register-to-modal/register-to-modal.component';

@Component({
  selector: 'app-manage-participants',
  templateUrl: './manage-participants.component.html',
  styleUrls: ['./manage-participants.component.scss']
})
export class ManageParticipantsComponent implements OnInit {

  competitionId: number;
  page = 1;
  pageSize = 10;
  totalElements = 10;
  gradingGroups: SimpleGradingGroup[] = [];
  participants: UserDetail[] = [];
  searchParameters: PartFilterDto = {};
  inputChange: Subject<any> = new Subject();
  bulkMap: Map<number, UserDetail> = new Map<number, UserDetail>();
  masterBulked = false;

  myFlags: Map<number, SimpleFlagDto> = new Map();
  newFlagText = '';
  selectedFlag: string;
  newId = -1;

  constructor(
    private competitionService: CompetitionService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private toastr: ToastrService
  ) {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.onUrlChange();
    this.inputChange
      .pipe(debounceTime(200))
      .subscribe(e => {
        this.page = 1;
        this.fetchParticipants();
    });

    this.getManagedFlags();
  }

  onTopBarSelect(key: string) {
    if (key === 'accepted') {
      this.searchParameters.accepted = true;
    } else if (key === 'outstanding') {
      this.searchParameters.accepted = false;
    } else {
      delete this.searchParameters.accepted;
    }
    this.inputChange.next(key);
  }

  bulkUpdate(accepted?: boolean, groupId?: number) {
    this.bulkAction(participants => {
      const update: ParticipantManageDto[] = participants
        .map(p => ({userId: p.id, accepted, groupId}));
      this.competitionService.updateRegisteredParticipants(this.competitionId, update)
        .subscribe({
          next: data => {
            this.fetchParticipants();
          },
          error: err => {
            this.toastr.error(this.localize.oopsSomethingWentWrong);
            console.error(err);
          }
        });
    });
  }

  onSetActive(modalContent) {
    this.modalService.open(modalContent, {ariaLabelledBy: 'modal-basic-title'}).result.then(
      result => {
        this.bulkUpdate(result, null);
      }
    );
  }

  onSetGroup(modalContent) {
    this.modalService.open(modalContent, {ariaLabelledBy: 'modal-basic-title'}).result.then(
      result => {
        this.bulkUpdate(null, result);
      }
    );
  }

  onEditFlags(modalContent) {
    this.modalService.open(modalContent, {ariaLabelledBy: 'modal-basic-title'}).result.then(
      result => {
        if(result === 'add') {
          this.addFlags();
        } else if(result === 'remove') {
          this.removeFlags();
        }
      }
    );
  }

  bulkAction(action) {
    this.masterBulked = false;
    action(Array.from(this.bulkMap.values()));
    this.bulkMap.clear();
  }

  onBulk(bulked: boolean, record: UserDetail) {
    if (bulked) {
      this.bulkMap.set(record.id, record);
    } else {
      this.bulkMap.delete(record.id);
    }
  }

  onMasterBulk(bulked: boolean) {
    this.participants.forEach(p => {
      this.onBulk(bulked, p);
    });
  }

  onUrlChange() {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.competitionId = parseInt(params.id, 10);
        if (isNaN(this.competitionId)) {
          this.router.navigate(['/']);
        }
        this.fetchGradingGroups();
        this.fetchParticipants();
      }
    });
  }

  fetchGradingGroups() {
    this.competitionService.getGroups(this.competitionId)
      .subscribe({
          next: gradingGroups => {
            this.gradingGroups = gradingGroups;
          },
          error: err => {
            console.log(err);
          }
        }
      );
  }

  fetchParticipants() {
    this.competitionService
      .getManagedParticipants(
        this.competitionId,
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

  onPageChange() {
    this.fetchParticipants();
    this.masterBulked = false;
  }

  getGradingGroupTitle(groupId: number): string {
    if (!groupId || this.gradingGroups.length === 0) {
      return '';
    }
    const title = this.gradingGroups.find(elem => elem.id === groupId);
    return title ? title.title : '';
  }

  getAcceptedText(accepted: boolean): string {
    return accepted ? this.localize.accepted : this.localize.outstanding;
  }

  getManagedFlags() {
    this.competitionService.getManagedFlags(this.competitionId).subscribe({
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

  currentFlags(): SimpleFlagDto[] {
    return Array.from(this.myFlags.values());
  }

  mapFlags(flags: SimpleFlagDto[]): string {
    if(flags == null) {
      return '';
    }

    return flags.map(f => f.name).join(', ');
  }

  addFlags() {
    const flag = this.myFlags.get(parseInt(this.selectedFlag, 10));
    const users = Array.from(this.bulkMap.values());
    this.competitionService.addMemberFlags(this.competitionId, {flag, users}).subscribe({
      next: data => {
        this.fetchParticipants();
        this.getManagedFlags();
      },
      error: error => {
        this.toastr.error(error, 'Error setting flags');
        console.log(error);
      }
    });
  }

  removeFlags() {
    const flag = this.myFlags.get(parseInt(this.selectedFlag, 10));
    const users = Array.from(this.bulkMap.values());
    console.log({flag, users});
    this.competitionService.removeMemberFlags(this.competitionId, {flag, users}).subscribe({
      next: data => {
        this.fetchParticipants();
        this.getManagedFlags();
      },
      error: error => {
        this.toastr.error(error, 'Error setting flags');
      }
    });
  }

}
