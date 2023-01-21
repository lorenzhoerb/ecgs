/* eslint-disable eqeqeq*/
//disabling eqeqeq for this file because I actually want type conversion in most places
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { UserDetail } from 'src/app/dtos/user-detail';
import { JudgeInfoDto } from 'src/app/services/grade.service';
import { GradingGroupWithRegisterToDto } from '../../../../dtos/gradingGroupWithRegisterToDto';
import {
  ParticipantPartGroupResult,
  ParticipantPartResult,
  ParticipantResult,
  Station
} from '../../../../dtos/simpleCompetitionListDto';

@Component({
  selector: 'app-competition-grading-group',
  templateUrl: './competition-grading-group.component.html',
  styleUrls: ['./competition-grading-group.component.scss']
})
export class CompetitionGradingGroupComponent implements OnInit {
  @Input() gradingGroup: GradingGroupWithRegisterToDto;
  @Input() stationIndex = 0;
  @Output() participantChange = new EventEmitter<ParticipantPartGroupResult>();

  stations: Station[];
  formula: any;
  result: ParticipantResult[];
  _otherJudges: UserDetail[] = [];



  constructor() {
  }

  get otherJudges(): UserDetail[] {
    return this._otherJudges;
  }

  @Input()
  set otherJudges(otherJudges: UserDetail[]) {
    this._otherJudges = otherJudges;
  }


  ngOnInit(): void {
    this.formula = JSON.parse(this.gradingGroup.gradingSystem.formula);
    this.stations = this.formula.stations;
    console.log(this.stations[this.stationIndex]);
  }


  getResultForStation(station: Station, registration: any) {
    const res: any = Object.assign({}, registration);
    const otherJudgeIds = this.otherJudges.map(o => o.id);

    const grade = res.stations
      .find(s => s.id == station.id).grades
      .filter(gd => !otherJudgeIds.includes(gd.judgeId));

    if (grade === null || grade === undefined || grade.length !== 1) {
      return {
        stationId: station.id,
        variables: station.variables.map(v => ({
            id: v.id,
            value: null
          }))
      };
    }

    //console.log(grade);
    const g = JSON.parse(grade[0].grade);
    const grades = g.grades;
    if(grades === null || grades === undefined || grades.length !== station.variables.length) {
      return {
        stationId: station.id,
        variables: station.variables.map(v => ({
            id: v.id,
            value: null
          }))
      };
    }

    return {
      stationId: station.id,
      variables: grades,
      hasError: grade[0].hasError,
      errorMessage: grade[0].errorMessage,
      isValid: grade[0].isValid,
      result: grade[0].result
    };
  }

  getParticipantsForStation(station: Station, registration: any) {
    const res: any = Object.assign({}, registration);
    const otherJudgeIds = this.otherJudges.map(o => o.id);

    return [...res.stations
      .find(s => s.id == station.id).grades
      .filter(g => otherJudgeIds.includes(g.judgeId))];
  }

  receiveResults(value: ParticipantPartResult): void {
    this.participantChange.emit({
      gradingGroupId: this.gradingGroup.id,
      ...value
    });
  }

}
