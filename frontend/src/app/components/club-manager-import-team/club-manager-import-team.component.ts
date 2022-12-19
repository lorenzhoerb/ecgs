import { Component, Input, OnInit } from '@angular/core';
import { NgxCsvParser, NgxCSVParserError } from 'ngx-csv-parser';
import { ToastrService } from 'ngx-toastr';
import { ClubManagerTeamImportDto, ClubManagerTeamMemberImportDto } from 'src/app/dtos/club-manager-team';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-club-manager-import-team',
  templateUrl: './club-manager-import-team.component.html',
  styleUrls: ['./club-manager-import-team.component.scss']
})
export class ClubManagerImportTeamComponent implements OnInit {
  @Input()
  currentPage = 1;

  @Input()
  teamName = '';

  violationNotificationLimit = 3;

  membersPerPage = 25;
  csvHeaders = {
    forCsv: ['firstName', 'lastName', 'email', 'gender', 'dateOfBirth'],
    forTableHeader: ['First name', 'Last name', 'Email', 'Gender', 'Date of birth'],
  };

  teamMembers: ClubManagerTeamMemberImportDto[] = [
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
    {firstName:'fn',lastName:'ln',email:'kek@kek.com',gender:'MALE',dateOfBirth:'2022-10-01'},
    {firstName:'fnSecond',lastName:'lnSecond',email:'kek2@kek.com',gender:'MALE',dateOfBirth:'2022-10-02'},
    {firstName:'fnThird',lastName:'lnThird',email:'kek3@kek.com',gender:'FEMALE',dateOfBirth:'2022-10-03'},
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
    console.log(123123);
    const file: File = event.target.files[0];

    this.ngxCsvParser.parse(file, { header: true, delimiter: ',', encoding: 'utf8' })
      .pipe().subscribe({
        next: (result: ClubManagerTeamMemberImportDto[]): void => {
          // TODO: Can we trust result? What if some error happens that error: ()... does not
          // catch??? Because by default `result = any[] | NgxCSVParserError`
          // MAYBE/TODO: csv parse case insensitive.
          // this.teamMembers = result.map(function(teamMember) {
          //   ClubManagerImportTeamComponent.csvHeaders.forEach(header => {
          //     teamMember[Object.keys(teamMember).find(key => key.toLowerCase() === header.toLowerCase)]
          //   });
          // });
          // this.teamMembers = [...result, ...this.teamMembers];
          this.teamMembers = result;
          this.notificiation.success(`Imported ${result.length} members.`);
        },
        error: (error: NgxCSVParserError): void => {
          this.notificiation.success(error.message, 'Error');
        }
      });
  }

  public onClearClicked() {
    this.teamMembers = [];
  }

  public onSaveClicked(): void {
  const team: ClubManagerTeamImportDto = {
      teamName: this.teamName,
      teamMembers: this.teamMembers,
    };
    this.userService.importTeam(team).subscribe(
      {
        next: (resp) => {
          this.notificiation.success(resp.message);
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

  public getInputTypeForHeader(header: string): string {
    switch(header) {
      case 'dateOfBirth': {
        return 'date';
      }
      default: {
        return 'text';
      }
    }
  }

  public onPageChangeClick(change: number) {
    if (change > 0 && Math.ceil(this.teamMembers.length / this.membersPerPage) - this.currentPage <= 0) {
      return;
    }
    if (change < 0 && this.currentPage + change <= 0) {
      return;
    }
    this.currentPage += change;
  }

  public getTeamMembersForCurrentPage(): ClubManagerTeamMemberImportDto[] {
    const lowerBound = (this.currentPage - 1) * this.membersPerPage;
    const upperBound = lowerBound + this.membersPerPage;

    return this.teamMembers.slice(lowerBound, upperBound);
  }

  public addNewMember() {
    this.teamMembers = [{
      firstName: '',
      lastName: '',
      email: '',
      gender: 'OTHER',
      dateOfBirth: ''
    }, ...this.teamMembers];
  }
}
