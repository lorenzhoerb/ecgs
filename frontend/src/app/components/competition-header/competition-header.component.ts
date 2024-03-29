import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-competition-header',
  templateUrl: './competition-header.component.html',
  styleUrls: ['./competition-header.component.scss']
})
export class CompetitionHeaderComponent implements OnInit, AfterViewInit {

  @ViewChild('header', {read: ElementRef})
  header: ElementRef;

  @ViewChild('ob', {read: ElementRef})
  ob: ElementRef;

  @Input() title = '...';

  @Input() isRegistered = false;

  @Input() canRegister: boolean;

  @Input() isEdit = false;

  @Output() register = new EventEmitter<void>();

  @Input() canEdit = false;

  @Output() edit = new EventEmitter<void>();

  @Input() canGrade = false;

  @Output() grading = new EventEmitter<void>();

  @Input() canCalculateCompetitionResults = false;

  @Output() competitionResultsCalculation = new EventEmitter<void>();

  @Input() reportsAreDownloadable = false;

  @Output() downloadReportDialogOpen = new EventEmitter<void>();

  @Output() participantsOpen = new EventEmitter<void>();
  @Output() groupsOpen = new EventEmitter<void>();

  hideManageModal = true;
  intersectionObserver?: IntersectionObserver;

  constructor() {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }

  showVerwalten(): boolean {
    return this.reportsAreDownloadable
      || this.canCalculateCompetitionResults
      || this.canEdit
      || this.canGrade;
  }

  ngAfterViewInit(): void {
    this.intersectionObserver = new IntersectionObserver(entries => {
      if (entries[0].intersectionRatio === 0) {
        this.header.nativeElement.classList.add('stuck');
        //newEl.classList.add("sticky-observer");
      } else if (entries[0].intersectionRatio === 1) {
        this.header.nativeElement.classList.remove('stuck');
        //newEl.classList.remove("sticky-observer");
      }
    }, {threshold: [0, 1]});

    this.intersectionObserver.observe(this.ob.nativeElement);
  }

  onRegisterClick() {
    this.register.emit();
  }

  onEditClick() {
    this.edit.emit();
  }

  onGradingClick() {
    this.grading.emit();
  }

  onCalculateCompetitionResults() {
    this.competitionResultsCalculation.emit();
  }

  onParticipantClick() {
    this.participantsOpen.emit();
  }

  onGroupClick() {
    this.groupsOpen.emit();
  }

  onDownloadReportClick() {
    this.downloadReportDialogOpen.emit();
  }
}
