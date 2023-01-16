import {Component, OnInit} from '@angular/core';
import {CompetitionService} from '../../../services/competition.service';
import {ActivatedRoute, Router} from '@angular/router';
import {debounceTime, map, of, Subject} from 'rxjs';
import {UserDetail} from '../../../dtos/user-detail';
import {SimpleGradingGroup} from '../../../dtos/simple-grading-group';
import {PartFilterDto} from '../../../dtos/part-filter-dto';
import {ParticipantManageDto} from '../../../dtos/participant-manage-dto';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import LocalizationService, {LocalizeService} from '../../../services/localization/localization.service';
import {ToastrService} from 'ngx-toastr';

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
  bulkStates = Array(this.pageSize).fill(false);
  masterBulked = false;

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

  bulkAction(action) {
    this.masterBulked = false;
    action(Array.from(this.bulkMap.values()));
    this.toggleAllBulk(false);
    this.bulkMap.clear();
  }

  toggleAllBulk(bulked: boolean) {
    this.bulkStates = Array(this.pageSize).fill(bulked);
  }

  onBulk(bulked: boolean, record: UserDetail) {
    if (bulked) {
      this.bulkMap.set(record.id, record);
    } else {
      this.bulkMap.delete(record.id);
    }
  }

  onMasterBulk(bulked: boolean) {
    this.toggleAllBulk(bulked);
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
            //TODO: error handling
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
    this.toggleAllBulk(false);
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
}
