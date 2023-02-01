/* eslint-disable eqeqeq*/
//disabling eqeqeq for this file because I actually want type conversion in most places

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { List } from 'lodash';
import { ToastrService } from 'ngx-toastr';
import { GradeDto } from 'src/app/dtos/gradeDto';
import { GradingGroupWithRegisterToDto } from 'src/app/dtos/gradingGroupWithRegisterToDto';
import { LiveResultDto } from 'src/app/dtos/liveResultDto';
import { UserDetail } from 'src/app/dtos/user-detail';
import { CompetitionService } from 'src/app/services/competition.service';
import { LiveResultsService } from 'src/app/services/live-results.service';
import { UserService } from 'src/app/services/user.service';
import LocalizationService, { LocalizeService } from '../../../services/localization/localization.service';

@Component({
  selector: 'app-live-results',
  templateUrl: './live-results.component.html',
  styleUrls: ['./live-results.component.scss']
})
export class LiveResultsComponent implements OnInit, OnDestroy {

  @Input() competitionId = -1;
  @Input() small = true;

  results: LiveResultDto[] = [];
  filteredResults: LiveResultDto[] = [];
  knownJudges: UserDetail[] = [];
  judgesPlaceHolder: number[] = [];
  gradingGroups: GradingGroupWithRegisterToDto[] = [];

  filterName = '';
  filterNameModel = '';
  filterGroup = '';
  filterGroupModel = '';
  filterStation = '';
  filterStationModel = '';

  notConnected = false;

  constructor(private service: LiveResultsService,
    private toastr: ToastrService,
    private userService: UserService,
    private competitionService: CompetitionService) { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.service.resultEvent$.subscribe(res => {
      this.newResult(res);
    });

    this.service.initialize(this.competitionId);

    this.getInitialData();
  }

  ngOnDestroy(): void {
    this.service.closeAll();
  }

  getResults(): LiveResultDto[] {
    if (this.small) {
      return this.results.slice(0, 6);
    } else {

      const filteredGroups =
        this.gradingGroups
          .filter(grp => grp.title.toLowerCase().includes(this.filterGroup))
          .map(grp => grp.id);

      const filteredParticipants =
          this.gradingGroups
            .flatMap(grp => grp.registrations)
            .filter(reg => `${reg.participant.firstName} ${reg.participant.lastName}`.toLowerCase().includes(this.filterName))
            .map(reg => reg.participant.id);

      const filteredStations =
            this.gradingGroups
              .flatMap(grp => (grp as any).stations)
              .filter(stat => stat.displayName.toLowerCase().includes(this.filterStation))
              .map(stat => stat.id);

      const filteredResults =
        this.results
          .filter(res => filteredGroups.includes(res.grades[0].gradingGroupId)
                && filteredParticipants.includes(res.grades[0].participantId)
                && filteredStations.includes(res.grades[0].stationId));


      return filteredResults;
    }
  }


  clearFilter() {
    this.filterNameModel = '';
    this.filterGroupModel = '';
    this.filterStationModel = '';

    this.onFilterInputChanged();
  }

  onFilterInputChanged() {
    this.filterName = this.filterNameModel.toLocaleLowerCase();
    this.filterGroup = this.filterGroupModel.toLocaleLowerCase();
    this.filterStation = this.filterStationModel.toLocaleLowerCase();

    this.results.forEach(res => (res as any).new = false);
  }



  openModal(result) {
    document.documentElement.style.overflow = 'hidden';

    result.modal = true;
  }

  closeModal(result) {
    document.documentElement.style.overflow = 'scroll';

    result.modal = false;
  }

  getGradeHeader(result: LiveResultDto) {
    const gradingGroup = this.gradingGroups.find(x => x.id == result.grades[0].gradingGroupId);

    if (gradingGroup === undefined) {
      return;
    }

    const station = (gradingGroup as any).stations.find(x => x.id == result.grades[0].stationId);

    if (station === undefined) {
      return;
    }

    return station.variables.map(x => x.displayName);
  }

  getGrades(result: LiveResultDto) {
    return result.grades.map(grade => {
      const variables = JSON.parse(grade.grade);
      console.log(variables);

      return {
        judge: this.getKnownJugde(grade.judgeId),
        variables
      };
    });
  }

  getPreviousGrades(result: LiveResultDto) {
    return (result as any).previous.map(grade => {
      const variables = JSON.parse(grade.grade);
      console.log(variables);

      return {
        judge: this.getKnownJugde(grade.judgeId),
        variables
      };
    });
  }

  getSizeClass() {
    const large = !this.small;

    return {
      largeres: large,
      smallres: !large
    };
  }

  getParticipatNumber(result: LiveResultDto): string {
    if (result.grades.length === 0) {
      return '';
    }

    const participantId = result.grades[0].participantId;

    const participant =
      this.gradingGroups
        .flatMap(group => group.registrations)
        .find(part => part.participant.id === participantId);

    return participant === undefined
      ? ''
      : `# ${participant.id}`;
  }

  getParticipantName(result: LiveResultDto): string {
    if (result.grades.length === 0) {
      return '';
    }

    const participantId = result.grades[0].participantId;

    const participant =
      this.gradingGroups
        .flatMap(group => group.registrations)
        .find(part => part.participant.id === participantId);

    return participant === undefined
      ? ''
      : `${participant.participant.firstName} ${participant.participant.lastName}`;
  }

  getGradingGroupTitle(result: LiveResultDto): string {
    if (result.grades.length === 0) {
      return '';
    }

    const gradingGroupId = result.grades[0].gradingGroupId;

    const gradingGroup =
      this.gradingGroups
        .find(group => group.id === gradingGroupId);

    return gradingGroup === undefined
      ? ' - '
      : `${gradingGroup.title}`;
  }

  getStationName(result: LiveResultDto): string {
    if (result.grades.length === 0) {
      return '';
    }

    const stationId = result.grades[0].stationId;

    const station =
      this.gradingGroups
        .flatMap(group => (group as any).stations)
        .find(st => st.id === stationId);

    return station === undefined
      ? ' - '
      : `${station.displayName}`;
  }

  getResult(result: LiveResultDto): string {
    if (result.grades.length === 0) {
      return '';
    }

    return `${result.grades[0].result}`;
  }

  getJudgeName(judgeId: number): string {
    const judge = this.getKnownJugde(judgeId);

    return judge === null
      ? 'Unbekannt...'
      : `${judge.firstName} ${judge.lastName}`;
  }

  newResult(result: LiveResultDto) {
    for (const grade of result.grades) {
      if (this.judgesPlaceHolder.find(pHolder => pHolder === grade.judgeId) === undefined) {
        this.judgesPlaceHolder.push(grade.judgeId);
        this.loadJudge(grade.judgeId);
      }
    }

    const existing = this.results.findIndex(re => re.grades[0].competitionId == result.grades[0].competitionId
          && re.grades[0].stationId == result.grades[0].stationId
          && re.grades[0].participantId == result.grades[0].participantId);


    if(existing !== -1) {
      (result as any).updated = true;

      (result as any).previous = this.results[existing].grades;
      this.results.splice(existing,1);
    }

    (result as any).new = true;

    this.results.forEach(res => (res as any).new = false);

    this.results.unshift(result);
  }

  getKnownJugde(judgeId: number): UserDetail | null {
    const judge = this.knownJudges.find(j => j.id === judgeId);

    return judge === undefined || judge === null ? null : judge;
  }

  loadJudge(judgeId): void {
    this.userService.getUserDetail(judgeId).subscribe({
      next: data => {
        if (this.getKnownJugde(judgeId) === null) {
          this.knownJudges.push(data);
        }
      }
    });
  }

  getInitialData() {
    this.competitionService.getGroupsWithRegistrations(this.competitionId).subscribe({
      next: data => {
        this.gradingGroups = data.map(x => Object.assign(x, { stations: JSON.parse(x.gradingSystem.formula).stations }));

        this.service.getAllResults(this.competitionId).subscribe({
          next: data2 => {
            data2.forEach(result => this.newResult(result));
          },
          error: error2 => {
            this.notConnected = true;
            //this.toastr.error(error2, 'Error fetching grading and registration data');
          }
        });
      },
      error: error => {
        this.notConnected = true;
        //this.toastr.error(error, 'Error fetching grading and registration data');
      }
    });
  }

}
