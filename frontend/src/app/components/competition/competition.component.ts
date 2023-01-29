import {Component, OnInit} from '@angular/core';
import {CompetitionService} from '../../services/competition.service';
import {Competition} from '../../dtos/competition';
import {ActivatedRoute, Router} from '@angular/router';
import {SupportedLanguages} from '../../services/localization/language';
import LocalizationService, {LocalizeService} from '../../services/localization/localization.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {RegisterModalComponent} from './register-modal/register-modal.component';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';
import {genderMap, UserDetail, UserDetailGrade} from '../../dtos/user-detail';
import { SimpleGradingGroup } from 'src/app/dtos/simple-grading-group';
import { ToastrService } from 'ngx-toastr';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DownloadReportDialogComponent } from '../download-report-dialog/download-report-dialog.component';
import { RequestErrorHandlerService } from 'src/app/services/request-error-handler.service';
import {UserDetailFilterDto} from '../../dtos/userDetailFilterDto';
import {debounceTime, Subject} from 'rxjs';
import {ParticipantManageDto} from '../../dtos/participant-manage-dto';
import {PartFilterDto} from '../../dtos/part-filter-dto';
import {SafeUrl} from '@angular/platform-browser';
import {GradingGroupService} from '../../services/grading-group.service';
import {Globals} from '../../global/globals';

@Component({
  selector: 'app-competition-view',
  templateUrl: './competition.component.html',
  styleUrls: ['./competition.component.scss']
})

export class CompetitionComponent implements OnInit {
  id: number;
  competition: Competition = null;
  error: Error = null;
  currentLanguage = SupportedLanguages.German;
  isRegisteredToCompetition = false;
  canRegister = false;
  participants: UserDetail[];
  groups: SimpleGradingGroup[];
  isCreator = false;
  //change this to false when isJudge is implemented
  isJudge = true;
  updateCounter = 0;
  imageUrl = '../../../assets/turnier.jpg';
  searchParameters: UserDetailFilterDto = {};
  inputChange: Subject<any> = new Subject();
  page = 1;
  pageSize = 10;
  totalElements = 10;
  reportsAreDownloadable = false;

  downloadable = false;
  selectedId: string;
  currentGroup: SimpleGradingGroup;
  groupsearchParameters: UserDetailFilterDto = {};
  groupparticipants: UserDetailGrade[] = [];
  groupinputChange: Subject<any> = new Subject();
  grouppage = 1;
  grouppageSize = 10;
  grouptotalElements = 10;

  lResults = false;

  constructor(private service: CompetitionService,
              private router: Router,
              private route: ActivatedRoute,
              private modalService: NgbModal,
              private gradingGroupService: GradingGroupService,
              private userService: UserService,
              private toastr: ToastrService,
              private globals: Globals,
              private authService: AuthService,
              private dialog: MatDialog,
              private errorHandler: RequestErrorHandlerService
              ) {
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
            if (this.competition.picturePath) {
              this.imageUrl = this.globals.backendOrigin + '/' + this.competition.picturePath;
            }
            this.error = null;
            this.service.getGroups(this.id).subscribe({
              next: data2 => {
                this.groups = data2;
                this.initCanRegister();

                this.service.checkIfReportsAreDownloadReady(this.id).subscribe({
                  next: ready => {
                    if(ready.downloadable === true) {
                      this.downloadable = true;
                      this.currentGroup = this.groups[0];
                      this.selectedId = String(this.currentGroup.id);
                      this.groupinputChange
                        .pipe(debounceTime(200))
                        .subscribe(e => {
                          this.currentGroup = this.groups.filter(g => g.id === parseInt(this.selectedId,10))[0];
                          this.grouppage = 1;
                          this.fetchResults();
                        });
                      this.fetchResults();
                    }
                  },
                  error: err => console.log(err)
                });
              },
              error: err => console.log(err)
            });
            this.fetchIsRegistered(this.id);
            this.fetchParticipants();
          },
          error: err => this.errorHandler.defaultErrorhandle(err)
        });

        this.service.getCompetitionByIdDetail(this.id).subscribe({
          next: () => {
            this.isCreator = true;
          }
        });

        this.service.getCompetitionByIdDetail(this.id).subscribe({
          //check and set permission if logged in user is judge
        });

        this.fetchIfReportsAreAvailable();
      }
    });

    this.inputChange
      .pipe(debounceTime(200))
      .subscribe(e => {
        this.page = 1;
        this.fetchParticipants();
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
  }

  pictureEmpty() {
    console.log(this.competition.picturePath);
    return this.competition.picturePath === null || this.competition.picturePath === undefined;
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

  largeResults(event) {
    if(!event) {
      document.documentElement.style.overflow = 'scroll';
    } else {
      document.documentElement.style.overflow = 'hidden';
    }
    this.lResults = event;
  }

  isLargeResults() {
    const large = this.lResults;
    return {
      largeres: large,
      smallres: !large
    };
  }

  onEdit() {
    this.router.navigate(['/competition/edit', this.id]);
  }

  onGrading() {
    this.router.navigate(['competition/' + this.id + '/grading']);
  }

  onDownloadReportClick() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.panelClass = 'mat-dialog-container-no-padding';

    dialogConfig.data = {
      competitionId: this.id
    };

    const dialogRef = this.dialog.open(DownloadReportDialogComponent, {
      ...dialogConfig,
      panelClass: 'mat-dialog-container-no-padding'
    });
  }

  onReportResultsCalculation() {
    this.service.calculateReportResults(this.id).subscribe({
      next: data => {
        this.toastr.success('Other may now generate/download up-to-date reports.', 'Calculation of final results succeeded!');
      }, error: err => this.errorHandler.defaultErrorhandle(err)
    });
  }

  updateParticipants() {
    this.updateCounter++;
  }

  mapParticipant(p: UserDetail): any[] {
    return [p.firstName, p.lastName, genderMap.get(p.gender), p.dateOfBirth.toLocaleDateString()];
  }

  fetchParticipants() {
    this.service
      .getParticipants(
        this.id,
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

  fetchResults() {
    this.gradingGroupService
      .getParticipants(
        this.currentGroup.id,
        this.groupsearchParameters,
        this.grouppage - 1,
        this.grouppageSize).subscribe({
      next: data => {
        this.groupparticipants = data.content;
        this.grouppageSize = data.size;
        this.grouptotalElements = data.totalElements;
        this.grouppage = data.pageable.pageNumber + 1;
      },
      error: err => {
        this.router.navigate(['/']);
      }
    });

  }

  fetchIfReportsAreAvailable(): void {
    this.service.checkIfReportsAreDownloadReady(this.id).subscribe({
      next: data => {
        this.reportsAreDownloadable = data.downloadable;
        console.log(this.reportsAreDownloadable);
      }, error: err => this.errorHandler.defaultErrorhandle(err)
    });
  }
}
