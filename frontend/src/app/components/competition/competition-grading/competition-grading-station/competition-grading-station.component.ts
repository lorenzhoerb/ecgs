/* eslint-disable eqeqeq*/
//disabling eqeqeq for this file because I actually want type conversion in most places
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import {
  ParticipantPartResult,
  ParticipantResult,
  Station,
  StationResults
} from '../../../../dtos/simpleCompetitionListDto';
import { ParticipantDetailDto } from '../../../../dtos/participantDetailDto';
import { JudgeInfoDto } from 'src/app/services/grade.service';
import { UserDetail } from 'src/app/dtos/user-detail';

@Component({
  selector: 'app-competition-grading-station',
  templateUrl: './competition-grading-station.component.html',
  styleUrls: ['./competition-grading-station.component.scss']
})
export class CompetitionGradingStationComponent implements OnInit {

  @Input() station: Station;
  @Input() participantDetailDto: ParticipantDetailDto;
  @Output() participantChange = new EventEmitter<ParticipantPartResult>();

  otherGrades: any[] = [];
  results?: StationResults = null;
  _otherJudges: UserDetail[] = [];
  isPending = false;
  private hasChanges = false;

  constructor() {
  }

  get otherJudges(): UserDetail[] {
    return this._otherJudges;
  }

  @Input()
  set otherJudges(otherJudges: UserDetail[]) {
    this._otherJudges = otherJudges;
  }

  @Input() set grade(grade: StationResults) {
    if (!this.hasChanges) {
      this.results = grade;
    }
    this.isPending = false;
  }

  @Input() set otherGrade(other: any[]) {
    if (this.otherGrades) {
      for (const change of other) {
        const prev = this.otherGrades.find(gd => gd.judgeId == change.judgeId);

        if ((prev && change.grade != prev.grade) || (prev === undefined && change.uuid)) {
          change.highlight = true;
          setTimeout(() => {
            change.highlight = false;
          }, 3000);
        }
      }
    }

    this.otherGrades = other;
  }

  ngOnInit(): void {
  }

  isHidden() {
    return (this.participantDetailDto as any).hidden;
  }

  gradeForJudge(judge, variableId) {
    const grade = this.otherGrades.find(g => g.judgeId == judge.id);
    return grade === null || grade === undefined ? '-' : (() => {
      const g = JSON.parse(grade.grade);
      const res = g.grades.find(x => x.id === variableId);
      return res === null || res === undefined ? '-' : res.value;
    })();
  }

  getErrorStyle() {
    const error = (this.results as any).hasError === undefined ? false : (this.results as any).hasError;
    return {
      error
    };
  }

  getModel(variableId: number) {
    if (!this.results.variables.find(v => v.id == variableId).value) {
      return null;
    }
    return parseFloat(this.results.variables.find(v => v.id == variableId).value);
  }



  pending() {
    const pending = (this.participantDetailDto as any).isPending == true ? true : false;

    return pending;
  }

  getLoaderClass() {
    const hidden = !this.pending();

    return {
      hide: hidden
    };
  }

  getResultText() {
    if (this.results === null ||
      (this.results as any).result === undefined ||
      (this.results as any).result === null ||
      !isFinite((this.results as any).result)) {
      return 'Unvollständig';
    }
    return `= ${(this.results as any).result.toFixed(2)} `;
  }

  getHighlightClass(judge) {
    const grade = this.otherGrades.find(g => g.judgeId == judge.id);
    if (grade === null || grade === undefined) {
      return {
        highlight: false
      };
    }

    const highlight = grade.highlight === undefined ? false : grade.highlight;

    return {
      highlight
    };
  }

  onInputChange(value, stationId, variableId) {
    this.hasChanges = true;
    const station = this.results;
    if (!station) {
      this.results = {
        stationId,
        variables: [
          {
            id: variableId,
            value: parseFloat(value.target.value)
          }
        ]
      };
    } else {
      const find = station.variables.find(x => x.id === variableId);
      if (find) {
        find.value = parseFloat(value.target.value);
      } else {
        station.variables.push({
          id: variableId,
          value: parseFloat(value.target.value)
        });
      }
    }
    console.log(this.participantDetailDto);
  }

  sendResults() {
    if(!this.hasChanges || this.pending()) {
      return;
    }

    this.participantChange.emit({
      participantId: this.participantDetailDto.participant.id,
      result: this.results
    });
    this.hasChanges = false;
  }

  getAcceptState() {
    const error = (this.results as any).hasError === undefined ? false : (this.results as any).hasError;
    return !this.hasChanges && !this.pending()
      && this.results.variables.find(v => v.value === null) === undefined
      && !error;
  }

  getErrorState() {
    const error = (this.results as any).hasError === undefined ? false : (this.results as any).hasError;
    return !this.hasChanges  && !this.pending() && error;
  }

  getErrorMessage() {
    return (this.results as any).errorMessage;
  }

  getAcceptStateClass() {
    const accept = this.getAcceptState();
    const error = this.getErrorState();
    return {
      accepted: accept,
      errorstate: error,
      changes: this.hasChanges
    };
  }


  calcInactive(judge: any) {
    const res = judge.inactive;

    return { inactive: res };
  }

}
