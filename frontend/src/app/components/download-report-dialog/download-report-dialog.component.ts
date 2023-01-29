import { HttpClient } from '@angular/common/http';
import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { DownloadReportRequestDto, DownloadReportRequestInclusionRule } from 'src/app/dtos/excel-download-request-dto';
import { ReportDownloadOptions as ReportDownloadInclusionRuleOptionsDto } from 'src/app/dtos/report-download-options';
import { SimpleGradingGroup } from 'src/app/dtos/simple-grading-group';
import { Globals } from 'src/app/global/globals';
import { CompetitionService } from 'src/app/services/competition.service';
import { RequestErrorHandlerService } from 'src/app/services/request-error-handler.service';
import LocalizationService, {LocalizeService} from '../../services/localization/localization.service';
import { CreateCompetitionSelectGradingSystemDialogComponent }
  from '../create-competition-select-grading-system-dialog/create-competition-select-grading-system-dialog.component';

@Component({
  selector: 'app-download-report-dialog',
  templateUrl: './download-report-dialog.component.html',
  styleUrls: ['./download-report-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DownloadReportDialogComponent implements OnInit {
  downloadReportRequestInclusionRule = DownloadReportRequestInclusionRule;
  downloadRequestDto: DownloadReportRequestDto = {
    gradingGroupsIds: [],
    inclusionRule: DownloadReportRequestInclusionRule.allParticipants
  };
  fetchedGradingGroups: SimpleGradingGroup[];
  competitionId: number;
  gradingGroupsSelection = {};
  reportDownloadInclusionRuleOptionsDto: ReportDownloadInclusionRuleOptionsDto;

  constructor(
    private service: CompetitionService,
    @Inject(MAT_DIALOG_DATA) data,
    private dialogRef: MatDialogRef<CreateCompetitionSelectGradingSystemDialogComponent>,
    private globals: Globals,
    private httpClient: HttpClient,
    private toastr: ToastrService,
    private errorHandler: RequestErrorHandlerService
  ) {
    this.competitionId = data.competitionId;
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.fetchGradingGroups();
    this.fetchCurrentUsersReportDownloadOptions();
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  downloadReport(): void {
    if (this.downloadRequestDto.inclusionRule === DownloadReportRequestInclusionRule.allParticipants) {
      this.downloadRequestDto.gradingGroupsIds = this.fetchedGradingGroups.map(g => g.id);
    } else {
      this.downloadRequestDto.gradingGroupsIds = Object.entries(this.gradingGroupsSelection).filter(
        ([k, v]) => v
        ).map(([k, v]) => Number.parseInt(k, 10));
    }
    console.log(this.downloadRequestDto);
    this.service.downloadReport(this.competitionId, this.downloadRequestDto).subscribe(
      {
        next: data => {
          if (data.name === null) {
            this.toastr.error('Reports are not downloadable yet');
            return;
          }
          const url = `${this.globals.backendOrigin}reports/${data.name}`;
          this.httpClient.head(url).subscribe({
            next: _data => {
              const a = document.createElement('a');
              a.href = url;
              a.click();
              a.remove();
            }, error: err => {

            }
          });
        }, error: err => this.errorHandler.defaultErrorhandle(err)
      }
    );
  }

  onSelectAllClick(): void {
    const allWereTrue = Object.values(this.gradingGroupsSelection).every(g => g);
    Object.keys(this.gradingGroupsSelection).forEach(
      k => {
        this.gradingGroupsSelection[k] = !allWereTrue;
      }
    );
  }

  onChangeInclusionRuleClick(rule: DownloadReportRequestInclusionRule): void {
    if (rule === DownloadReportRequestInclusionRule.allParticipants) {
      Object.keys(this.gradingGroupsSelection).forEach(
        k => {
          this.gradingGroupsSelection[k] = false;
        }
      );
    } else if (rule === DownloadReportRequestInclusionRule.onlyYou
      && !this.reportDownloadInclusionRuleOptionsDto?.canGenerateReportForSelf) {
      return;
    } else if (rule === DownloadReportRequestInclusionRule.onlyYourTeam
      && !this.reportDownloadInclusionRuleOptionsDto?.canGenerateReportForTeam) {
      return;
    }

    this.downloadRequestDto.inclusionRule = rule;
  }

  onSelectGradingGroup(id: number): void {
    this.gradingGroupsSelection[id] = !this.gradingGroupsSelection[id];
  }

  getClassForSelectedInclusionRule(rule: DownloadReportRequestInclusionRule): string {
    let clazz = '';
    if (rule === this.downloadRequestDto?.inclusionRule) {
      clazz += ' item-selected';
    }

    switch(rule){
      case DownloadReportRequestInclusionRule.onlyYou:
        if (!this.reportDownloadInclusionRuleOptionsDto?.canGenerateReportForSelf) {
          clazz += ' disabled-inclusion-rule';
        }
        break;
      case DownloadReportRequestInclusionRule.onlyYourTeam:
        if (!this.reportDownloadInclusionRuleOptionsDto?.canGenerateReportForTeam) {
          clazz += ' disabled-inclusion-rule';
        }
        break;
      default:
        break;
    }

    return clazz;
  }

  getClassForSelectedGradingGroup(id: number): string {
    if (this.gradingGroupsSelection[id]) {
      return 'item-selected';
    }

    return '';
  }

  fetchCurrentUsersReportDownloadOptions(): void {
    this.service.getCurrentUserReportDownloadOptions(this.competitionId).subscribe(
      {
        next: data => {
          this.reportDownloadInclusionRuleOptionsDto = data;
          console.log(data);
        }, error: err => this.errorHandler.defaultErrorhandle(err)
      }
    );
  }

  private fetchGradingGroups() {
    this.service.getGroups(this.competitionId).subscribe({
      next: data => {
        this.fetchedGradingGroups = data;
        data.map(d => d.id).forEach(
          id => {
            this.gradingGroupsSelection[id] = false;
          }
        );
      }, error: err => this.errorHandler.defaultErrorhandle(err)
    });
  }
}
