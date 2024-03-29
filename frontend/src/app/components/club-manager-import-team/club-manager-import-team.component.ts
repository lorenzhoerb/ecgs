import { Component, Input, OnInit } from '@angular/core';
import { NgxCsvParser, NgxCSVParserError } from 'ngx-csv-parser';
import { ToastrService } from 'ngx-toastr';
import { ClubManagerTeamImportDto, ClubManagerTeamMemberImportDto } from 'src/app/dtos/club-manager-team';
import { SupportedLanguages } from 'src/app/services/localization/language';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service';
import { UserService } from 'src/app/services/user.service';
import {MatSelectModule} from '@angular/material/select';

@Component({
  selector: 'app-club-manager-import-team',
  templateUrl: './club-manager-import-team.component.html',
  styleUrls: ['./club-manager-import-team.component.scss']
})
export class ClubManagerImportTeamComponent implements OnInit {
  @Input()
  page = 1;

  @Input()
  teamName = '';

  violationNotificationLimit = 3;

  pageSize = 25;
  csvHeaders = {
    forCsv: ['firstName', 'lastName', 'email', 'gender', 'dateOfBirth', 'flag'],
    forTableHeader: ['First name', 'Last name', 'Email', 'Gender', 'Date of birth'],
  };

  displayTeamMembers: ClubManagerTeamMemberImportDto[] = [];
  teamMembers: ClubManagerTeamMemberImportDto[] = [
  ];

  constructor(
    private notificiation: ToastrService,
    private userService: UserService,
    private ngxCsvParser: NgxCsvParser,
  ) { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.refreshCountries();
  }

  public onFileSelect(event: any): void {
    const file: File = event.target.files[0];

    this.ngxCsvParser.parse(file, { header: true, delimiter: ',', encoding: 'utf8' })
      .pipe().subscribe({
        next: (result: ClubManagerTeamMemberImportDto[]): void => {
          this.teamMembers = [...result, ...this.teamMembers];
          this.refreshCountries();
          this.notificiation.info(`Imported ${result.length} members.`);
        },
        error: (error: NgxCSVParserError): void => {
          this.notificiation.error(error.message, 'Error');
        }
      });
  }

  refreshCountries() {
    this.displayTeamMembers = this.teamMembers.slice(
      (this.page - 1) * this.pageSize,
      (this.page - 1) * this.pageSize + this.pageSize,
    );
  }

  public onFileClick(event: any) {
    event.target.value = null;
  }

  public onClearClicked() {
    this.teamMembers = [];
    this.page = 1;
    this.refreshCountries();
  }

  public onSaveClicked(): void {
    console.log(this.teamMembers);
    const team: ClubManagerTeamImportDto = {
        teamMembers: this.teamMembers,
      };
      this.userService.importTeam(team).subscribe(
        {
          next: (resp) => {
            this.notificiation.success(`Team ${this.teamName} received ${resp.newParticipantsCount} new
            participants (${resp.oldParticipantsCount} were already present/duplicates)`);
          },
          error: (err) => {
            console.log(err);
            const errorObj = err.error;
            if (err.status === 0) {
              this.notificiation.error('Could not connect to remote server!', 'Connection error');
            } else if (err.status === 401) {
              this.notificiation.error('Either you are not authenticated or your session has expired', 'Authentication error');
            } else if (err.status === 403) {
              this.notificiation.error('You don\'t have enought permissions', 'Authorization error');
            } else if (!errorObj.message && !errorObj.errors) {
              this.notificiation.error(err.message ?? '', 'Unexpected error occured.');
            } else {
              if (this.violationNotificationLimit < errorObj.errors.length) {
                this.notificiation.error(`There are ${errorObj.errors.length - this.violationNotificationLimit} more violations...`,
                  errorObj.message);
              }
              for (let i = 0; i < this.violationNotificationLimit && i < errorObj.errors.length; i++) {
                this.notificiation.error(errorObj.errors[i], errorObj.message ?? 'Error', );
              }
            }
          }
        }
      );
    }


  public onTeamNameChange(newTeamName: string): void {
    this.teamName = newTeamName;
  }

  public onTeamMemberFieldChange(value: any, index: number, header: string) {
    this.teamMembers[index][header] = value;
  }

  public onPageChangeClick(change: number) {
    if (change > 0 && Math.ceil(this.teamMembers.length / this.pageSize) - this.page <= 0) {
      return;
    }
    if (change < 0 && this.page + change <= 0) {
      return;
    }
    this.page += change;
  }

  public getTeamMembersForpage(): ClubManagerTeamMemberImportDto[] {
    const lowerBound = (this.page - 1) * this.pageSize;
    const upperBound = lowerBound + this.pageSize;

    return this.teamMembers.slice(lowerBound, upperBound);
  }

  public onAddNewMemberClick() {
    this.teamMembers = [{
      firstName: '',
      lastName: '',
      email: '',
      gender: 'OTHER',
      dateOfBirth: ''
    }, ...this.teamMembers];
    this.refreshCountries();
  }

  public onRemoveMemberClick(index: number) {
    this.teamMembers.splice(index, 1);
    if (this.teamMembers.length <= (this.page-1) * this.pageSize) {
      this.page--;
    }
    this.refreshCountries();
  }

  public getGlobalTeamMemberIndex(index: number) {
    return index + (this.page - 1) * this.pageSize;
  }

  public exportAsCSV() {
    const blob = new Blob([this.formatCSVForExport()], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    console.log(url);
    const tempAnchorTag = document.createElement('a');
    tempAnchorTag.href = url;
    tempAnchorTag.download = this.localize.getLanguage() === SupportedLanguages.English ? 'Exported-Team.csv' : 'Exportiertes-Team.csv';
    tempAnchorTag.click();
    tempAnchorTag.remove();
  }

  private formatCSVForExport() {
    const headerPart = this.csvHeaders.forCsv.join(',');
    const dataPart = this.teamMembers.map(member => this.csvHeaders.forCsv.map(header => member[header]).join(',')).join('\n');

    return `${headerPart}\n${dataPart}`;
  }

}
