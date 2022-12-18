import { Component, Input, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { CalendarViewCompetition, CalendarWeek } from 'src/app/dtos/competition';
import { formatDate } from '@angular/common';
import { ExtendedWeekInfo, WeekInfo } from 'src/app/interfaces/CompetitionCalendarView/WeekInfo';
import { UserService } from 'src/app/services/user.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-competition-calender-view',
  templateUrl: './competition-calender-view.component.html',
  styleUrls: ['./competition-calender-view.component.scss']
})
// @TODO: height depends on amount of competitions
export class CompetitionCalenderViewComponent implements OnInit {
  @Input()
  defaultWeekToShow: string;

  @Input()
  interactionText: string;
  selectedExtendedWeekInfo: ExtendedWeekInfo;

  competitions: CalendarViewCompetition[] = [];

  constructor(
    private userService: UserService,
    private notification: ToastrService
  ) { }

  ngOnInit(): void {
    this.setupDefaultSelectedWeekInfo();
    this.setupDefaultWeekToShow();
    this.fetchCompetitions();
  }

  setupDefaultWeekToShow(): void {
    this.defaultWeekToShow = `${this.selectedExtendedWeekInfo.year}-W${String(this.selectedExtendedWeekInfo.weekNumber).padStart(2, '0')}`;
  }

  setupDefaultSelectedWeekInfo(): void {
    const startOfSelectedWeek = new Date();
    while(startOfSelectedWeek.getDay() !== 1) {
      startOfSelectedWeek.setDate(startOfSelectedWeek.getDate() - 1);
    }

    const endOfSelectedWeek = new Date(startOfSelectedWeek);
    endOfSelectedWeek.setDate(endOfSelectedWeek.getDate() + 6);
    endOfSelectedWeek.setHours(23, 59, 59);

    this.selectedExtendedWeekInfo = {
      ...this.selectedExtendedWeekInfo,
      ...this.getTodaysWeekInfo(),
      start: startOfSelectedWeek,
      end: endOfSelectedWeek
    };
  }

  dayNumberToString(dayNumber: number): string {
    const dayToShow = new Date(this.selectedExtendedWeekInfo.start);
    dayToShow.setDate(dayToShow.getDate() + dayNumber - 1);

    return formatDate(dayToShow, 'EEE\ndd.MM', 'en-US').toString();
  }

  onWeekPickerChange(week: string) {
    this.competitions = [];
    const weekSplit = week.split('-');
    const weekNumber = Number.parseInt(weekSplit[1].substring(1), 10);

    if (weekNumber > 52 || weekNumber < 1) {
      this.interactionText = 'Failed: Invalid date requested\nNumber of the week should be from 1 up to 52';
      return;
    }

    const year = Number.parseInt(weekSplit[0], 10);

    const startOfThatYear = new Date(year, 0, 1);
    const firstMondayOfThatYear = new Date(startOfThatYear);
    while(firstMondayOfThatYear.getDay() !== 1) {
      firstMondayOfThatYear.setDate(firstMondayOfThatYear.getDate() + 1);
    }

    const startOfThatWeek = new Date(firstMondayOfThatYear);
    startOfThatWeek.setDate(startOfThatWeek.getDate() + 7 * (weekNumber - 1));

    const endOfThatWeek = new Date(startOfThatWeek);
    endOfThatWeek.setDate(endOfThatWeek.getDate() + 6);
    endOfThatWeek.setHours(23, 59, 59);

    this.selectedExtendedWeekInfo = {
      ...this.selectedExtendedWeekInfo,
      year,
      weekNumber,
      start: startOfThatWeek,
      end: endOfThatWeek
    };

    console.log(JSON.stringify(this.selectedExtendedWeekInfo));

    this.fetchCompetitions();
  }

  getTodaysWeekInfo(): WeekInfo {
    return this.getWeekInfoOfThatDay(new Date());
  }

  getWeekInfoOfThatDay(day: Date): WeekInfo {
    const mondayOfGivenWeek = new Date(day);
    while (mondayOfGivenWeek.getDay() !== 1) {
      mondayOfGivenWeek.setDate(mondayOfGivenWeek.getDate() - 1);
    }

    const startOfThisYear = new Date(mondayOfGivenWeek.getFullYear(), 0, 1);
    const numberOfDays =  Math.floor((mondayOfGivenWeek.getTime() - startOfThisYear.getTime())
     / (24 * 60 * 60 * 1000));

    const weekNumber = Math.ceil(numberOfDays / 7);

    return {
      weekNumber,
      year: mondayOfGivenWeek.getFullYear()
    };
  }

  // dynamic CSS for template

  getNextCompetitionGridArea(competition: CalendarViewCompetition, index: number): string {
    // eslint-disable-next-line max-len
    const competitionBeginAndCurrentMondayTimeDifference = competition.beginOfCompetition.getTime() - this.selectedExtendedWeekInfo.start.getTime();
    let columnStart: number;
    if (competitionBeginAndCurrentMondayTimeDifference <= 0) {
      columnStart = 1;
    } else {
      const dayNumber = competition.beginOfCompetition.getDay();
      columnStart = dayNumber === 0 ? 7 : dayNumber;
    }

    // eslint-disable-next-line max-len
    const competitionEndAndCurrentSundayTimeDifference = competition.endOfCompetition.getTime() - this.selectedExtendedWeekInfo.end.getTime();
    let columnEnd: number;
    if (competitionEndAndCurrentSundayTimeDifference >= 0) {
      columnEnd = 8;
    } else {
      const dayNumber = competition.endOfCompetition.getDay();
      columnEnd = 1 + (dayNumber === 0 ? 7 : dayNumber);
    }

    return `${index+1} / ${columnStart} / ${index+2} / ${columnEnd}`;
  }

  getNextCompetitionFontSize(competition: CalendarViewCompetition): string {
    // eslint-disable-next-line max-len
    const competitionBeginAndCurrentMondayTimeDifference = competition.beginOfCompetition.getTime() - this.selectedExtendedWeekInfo.start.getTime();
    let columnStart: number;
    if (competitionBeginAndCurrentMondayTimeDifference <= 0) {
      columnStart = 1;
    } else {
      const dayNumber = competition.beginOfCompetition.getDay();
      columnStart = dayNumber === 0 ? 7 : dayNumber;
    }

    // eslint-disable-next-line max-len
    const competitionEndAndCurrentSundayTimeDifference = competition.endOfCompetition.getTime() - this.selectedExtendedWeekInfo.end.getTime();
    let columnEnd: number;
    if (competitionEndAndCurrentSundayTimeDifference >= 0) {
      columnEnd = 8;
    } else {
      const dayNumber = competition.endOfCompetition.getDay();
      columnEnd = 1 + (dayNumber === 0 ? 7 : dayNumber);
    }

    return `${(80 + (columnEnd - columnStart)*20)}%`;
  }

  getDayBodyGridArea(index: number): string {
    return `1 / ${index} / ${this.competitions.length*3 + 6} / ${index+1}`;
  }

  // Rest related

  fetchCompetitions(): void {
    this.userService.getCompetitionsForCalender(this.selectedExtendedWeekInfo.year, this.selectedExtendedWeekInfo.weekNumber)
    .subscribe({
      next: (data) => {
        this.competitions = this.parseCompetitions(data);
      },
      error: (err) => {
        const errorObj = err.error;
        if (err.status === 0) {
          this.notification.error('Could not connect to remote server!', 'Connection error');
        } else if (err.status === 401) {
          this.notification.error('Either you are not authenticated or your session has expired', 'Authentication error');
        } else if (err.status === 403) {
          this.notification.error('You don\'t have enought permissions', 'Authorization error');
        } else if (!errorObj.message && !errorObj.errors) {
          this.notification.error(err.message ?? '', 'Unexpected error occured.');
        }
      }
    });
  }

  parseCompetitions(competitions: CalendarViewCompetition[]): CalendarViewCompetition[] {
    return competitions.map(comp => ({
      ...comp,
      beginOfCompetition: new Date(comp.beginOfCompetition),
      endOfCompetition: new Date(comp.endOfCompetition),
    }));
  }
}
