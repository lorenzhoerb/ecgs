import { Component, Input, OnInit } from '@angular/core';
import { NgxCsvParser, NgxCSVParserError } from 'ngx-csv-parser';
import { ToastrService } from 'ngx-toastr';
import { ClubManagerTeamImportDto, ClubManagerTeamMemberImportDto } from 'src/app/dtos/club-manager-team';
import { ImportFlag } from 'src/app/dtos/import-flag';
import { SupportedLanguages } from 'src/app/services/localization/language';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-import-flags',
  templateUrl: './import-flags.component.html',
  styleUrls: ['./import-flags.component.scss']
})
export class ImportFlagsComponent implements OnInit {
  currentPage = 1;
  violationNotificationLimit = 3;

  flagsPerPage = 25;
  csvHeaders = {
    forCsv: ['email', 'flag'],
  };

  flags: ImportFlag[] = [
    {email: 'chicka@easd.com', flag: 'cool guy'},
    {email: 'chicka2@easd.com', flag: 'nice guy'},
    {email: 'chicka3@easd.com', flag: 'ok guy'},
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
  }

  public onFileSelect(event: any): void {
    const file: File = event.target.files[0];

    this.ngxCsvParser.parse(file, { header: true, delimiter: ',', encoding: 'utf8' })
      .pipe().subscribe({
        next: (result: ImportFlag[]): void => {
          this.flags = [...result, ...this.flags];
          this.notificiation.info(`Imported ${result.length} flags.`);
        },
        error: (error: NgxCSVParserError): void => {
          this.notificiation.error(error.message, 'Error');
        }
      });
  }

  public onFileClick(event: any) {
    event.target.value = null;
  }

  public onClearClicked() {
    this.flags = [];
    this.currentPage = 1;
  }

  public getGlobalFlagIndex(index: number) {
    return index + (this.currentPage - 1) * this.flagsPerPage;
  }

  public onSaveClicked(): void {
    this.userService.importFlags(this.flags).subscribe(
      {
        next: (resp) => {
          if (resp.newImportedFlags === 0) {
            this.notificiation.success('All flags were already present.');
          } else {
            if (resp.newImportedFlags === 1) {
              this.notificiation.success(`Successfully imported ${resp.newImportedFlags} flag.`);
            } else {
              this.notificiation.success(`Successfully imported ${resp.newImportedFlags} flags.`);
            }
          }
        },
        error: (err) => {
          console.log(err);
          const errorObj = err.error;
          if (err.status === 0) {
            this.notificiation.error('Could not connect to remote server!', 'Connection error');
          } else if (err.status === 401) {
            this.notificiation.error('Either you are not authenticated or your session has expired', 'Authentication error');
          } else if (err.status === 403) {
            if (errorObj.message) {
              this.notificiation.error(`${errorObj.message}:\n${errorObj.errors.join('\n')}`, 'Authorization error');
            } else {
              this.notificiation.error('Insufficient permissions', 'Authorization error');
            }
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

  public onFlagFieldChange(value: any, index: number, header: string) {
    this.flags[index][header] = value;
  }

  public onPageChangeClick(change: number) {
    if (change > 0 && Math.ceil(this.flags.length / this.flagsPerPage) - this.currentPage <= 0) {
      return;
    }
    if (change < 0 && this.currentPage + change <= 0) {
      return;
    }
    this.currentPage += change;
  }

  public getFlagsForCurrentPage(): ImportFlag[] {
    const lowerBound = (this.currentPage - 1) * this.flagsPerPage;
    const upperBound = lowerBound + this.flagsPerPage;

    return this.flags.slice(lowerBound, upperBound);
  }

  public onAddNewFlagClick() {
    this.flags = [{
      email: '',
      flag: ''
    }, ...this.flags];
  }

  public onRemoveFlagClick(index: number) {
    this.flags.splice(index, 1);
    if (this.flags.length <= (this.currentPage-1) * this.flagsPerPage) {
      this.currentPage--;
    }
  }

  public exportAsCSV() {
    const blob = new Blob([this.formatCSVForExport()], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const tempAnchorTag = document.createElement('a');
    tempAnchorTag.href = url;
    tempAnchorTag.download = this.localize.getLanguage() === SupportedLanguages.English ? 'Exported-Flags.csv' : 'Exportierte-Flags.csv';
    tempAnchorTag.click();
    tempAnchorTag.remove();
  }

  private formatCSVForExport() {
    const headerPart = this.csvHeaders.forCsv.join(',');
    const dataPart = this.flags.map(member => this.csvHeaders.forCsv.map(header => member[header]).join(',')).join('\n');

    return `${headerPart}\n${dataPart}`;
  }
}
