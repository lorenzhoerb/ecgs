/* eslint-disable eqeqeq*/
//disabling eqeqeq for this file because I actually want type conversion in most places
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { GradingGroupWithRegisterToDto } from '../../../dtos/gradingGroupWithRegisterToDto';
import { CompetitionService } from '../../../services/competition.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from '../../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import {
  ParticipantPartGroupResult,
  ParticipantResult,
  ParticipantResultDTO, Station,
  StationResults
} from '../../../dtos/simpleCompetitionListDto';
import { StompService } from 'src/app/services/stomp.service';
import { AuthService } from 'src/app/services/auth.service';
import { GradeService, InfoType } from 'src/app/services/grade.service';
import { interval, Subscription } from 'rxjs';
import { GradeDto } from 'src/app/dtos/gradeDto';
import { MessageErrorType } from 'src/app/dtos/messageErrorDto';
import { cloneDeep } from 'lodash';

@Component({
  selector: 'app-competition-grading',
  templateUrl: './competition-grading.component.html',
  styleUrls: ['./competition-grading.component.scss']
})

export class CompetitionGradingComponent implements OnInit, OnDestroy {
  id: number;
  gradingGroups: any[];
  selectedGroup = 0;
  selectedStation = 0;
  stations: Station[];
  formula: any;

  loggedInStation = false;
  loggedInStationId = -1;
  loggedInGroupIds: number[] = [];

  currentTime: number;

  allGrades: any = {};
  knownJudges = [];
  pendingRequest = 0;
  pendingQueue = [];

  filterName = '';
  filterFlag = '';
  filterGroup = '';

  filterNameModel = '';
  filterFlagModel = '';
  filterGroupModel = '';
  filteredGroups = [];

  userId = -1;

  intervalTimer?: Subscription = null;

  pendingMessages: GradeDto[] = [];

  uniqueStations: Station[];
  websocketmsg = '';

  results: ParticipantResult[] = [];


  constructor(private service: CompetitionService,
    private route: ActivatedRoute,
    private gradeService: GradeService,
    private modalService: NgbModal,
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService) { }

  ngOnInit(): void {
    this.intervalTimer = interval(10 * 1000).subscribe(_ => {
      this.currentTime = Date.now();
      this.knownJudges.filter((judge, index, arr) => {
        if (judge.inactive) {
          let remove = true;

          for (const group of this.loggedInGroupIds) {
            const exists = this.allGrades[group].flatMap(g => g).findIndex(g => g.judgeId == judge.id);
            if (exists !== -1) {
              remove = false;
            }
          }

          if (remove) {
            arr.splice(index, 1);
          }
        }
      });
    });

    this.userService.getUserDetail(null).subscribe({
      next: detail => this.userId = detail.id,
      error: err => {
        this.toastr.error(err, 'Nicht angemeldet');
        this.router.navigate(['/']);
      }
    });
    this.subscribeToWebsocketEvents();

    this.route.params.subscribe(params => {
      if (params.id) {
        this.id = parseInt(params.id, 10);
        this.service.getGroupsWithRegistrations(this.id).subscribe({
          next: data => {
            this.gradingGroups = data.map(x => Object.assign(x, { stations: JSON.parse(x.gradingSystem.formula).stations }));
            this.filteredGroups = this.gradingGroups;

            for (const group of this.gradingGroups) {
              for (const reg of group.registrations) {
                reg.stations = group.stations.map(s => Object.assign({}, s, { grades: [] }));
              }
            }

            this.uniqueStations =
              this.gradingGroups
                .flatMap(x => x.stations)
                .filter((value, index, self) => index === self.findIndex(t => t.displayName === value.displayName));
          },
          error: error => {
            this.toastr.error(error, 'Error fetching grading and registration data');
          }
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.intervalTimer !== null) {
      this.intervalTimer.unsubscribe();
    }

    this.gradeService.closeAll();
  }

  loggoutFromStation() {
    for (const groupId of this.loggedInGroupIds) {
      this.gradeService.leaveStation(this.id, groupId, this.loggedInStationId, this.uniqueStations[this.selectedStation].displayName);
      this.allGrades[groupId] = [];
    }

    this.loggedInGroupIds = [];
    this.loggedInStationId = -1;
    this.loggedInStation = false;

    this.knownJudges.splice(0, this.knownJudges.length);
  }

  loggIntoStation() {
    this.loggedInStationId = this.uniqueStations[this.selectedStation].id;

    if (this.selectedGroup == -1) {
      this.loggedInGroupIds = this.gradingGroups
        .filter(g => g.stations.map(s => s.id).includes(this.loggedInStationId))
        .map(g => g.id);
    } else {
      this.loggedInGroupIds = [this.gradingGroups[this.selectedGroup].id];
    }

    this.filteredGroups =
      this.gradingGroups
        .filter(this.includeGroup.bind(this))
        .map(this.filterParticipants.bind(this))
        .filter(grp => (grp as any).registrations.length !== 0);

    this.loggedInStation = true;

    for (const groupId of this.loggedInGroupIds) {
      this.pendingRequest++;
      this.gradeService.getAllGrades(this.id, groupId, this.loggedInStationId).subscribe({
        next: (value) => {
          this.allGrades[groupId] = value;
          this.pendingRequest--;

          [... new Set(value.map(g => g.judgeId))].filter(
            j => j != this.userId
          ).forEach(judge => {

            if (this.knownJudges.filter(j => j.info.id == judge && j.info.belongsTo.gradingGroupId == groupId).length !== 1) {

              const judgeInfo = {
                id: judge, belongsTo: {
                  competitionId: this.id,
                  gradingGroupId: groupId,
                  stationId: this.loggedInStationId
                }
              };

              const ind = this.knownJudges.push({ info: judgeInfo }) - 1;
              this.userService.getUserDetail(judge).subscribe({
                next: (val) => {
                  this.knownJudges[ind] = Object.assign({}, val, { inactive: true, lastSeen: Date.now() - 5 * 1000, info: judgeInfo });
                },
                error: (err) => {
                  this.toastr.error(err);
                }
              });
            }
          });

          for (const grade of value) {
            this.setGrade(grade);
          }
        },
        error: (error) => {

        }
      });

      this.gradeService.enterStation(this.id, groupId, this.loggedInStationId, this.uniqueStations[this.selectedStation].displayName);
    }
  }

  clearFilter() {
    this.filterNameModel = '';
    this.filterGroupModel = '';
    this.filterFlagModel = '';

    this.onFilterInputChanged();
  }

  onFilterInputChanged() {
    this.filterName = this.filterNameModel.toLocaleLowerCase();
    this.filterGroup = this.filterGroupModel.toLocaleLowerCase();
    this.filterFlag = this.filterFlagModel.toLocaleLowerCase();

    this.filteredGroups =
      this.gradingGroups
        .filter(this.includeGroup.bind(this))
        .map(this.removeUuidFromExisting.bind(this))
        .map(this.filterParticipants.bind(this))
        .filter(grp => (grp as any).registrations.length !== 0);
  }

  filterParticipants(group: any): any {
    const inc = cloneDeep(group);

    if (this.filterName.trim() === '') {
      return group;
    }

    const removeList = [];

    for (const part of inc.registrations) {
      const ok = `${part.participant.firstName.toLowerCase()} ${part.participant.lastName.toLowerCase()}`
        .includes(this.filterName);

      if (!ok) {
        removeList.push(part);
      }
    }

    removeList.forEach(remove => {
      const ind = inc.registrations.findIndex(re => re.participant.id === remove.participant.id);
      inc.registrations.splice(ind, 1);
    });

    return inc;
  }

  includeGroup(group: any): boolean {
    let inc = true;

    if (!this.loggedInGroupIds.includes(group.id)) {
      inc = false;
    }

    if (this.filterGroup.trim() !== '' && !group.title.toLowerCase().includes(this.filterGroup)) {
      inc = false;
    }

    return inc;
  }

  getFilteredGradingGroups() {
    return this.filteredGroups.filter(g => this.loggedInGroupIds.includes(g.id));
  }

  removeUuidFromExisting(oGroup: any): any {
    const group = oGroup;

    for (const regist of group.registrations) {
      for (const sta of regist.stations) {
        for (const grade of sta.grades) {
          const gd = this.filteredGroups.find(x => x.id == grade.gradingGroupId);
          if (gd === undefined) {
            continue;
          }

          const reg = gd.registrations.find(r => r.participant.id == grade.participantId);
          if (reg === undefined) {
            continue;
          }

          const station = reg.stations.find(s => s.id == grade.stationId);
          if (station === undefined) {
            continue;
          }
          const existing = station.grades.findIndex(g => g.judgeId == grade.judgeId && g.uuid == grade.uuid);
          if (existing !== -1) {
            grade.uuid = null;
          }
        }
      }
    }


    return group;
  }

  setGrade(grade: any) {
    const gd = this.gradingGroups.find(x => x.id == grade.gradingGroupId);
    if (gd === null) {
      return;
    }

    const reg = gd.registrations.find(r => r.participant.id == grade.participantId);
    if (reg === null) {
      return;
    }

    const station = reg.stations.find(s => s.id == grade.stationId);
    if (station === null) {
      return;
    }
    const existing = station.grades.findIndex(g => g.judgeId == grade.judgeId);
    if (existing !== -1) {
      station.grades[existing] = grade;
    } else {
      station.grades.push(grade);
    }
  }

  getHeaderJudges(): any {
    return this.knownJudges
      .filter((x, ind, arr) => arr.findIndex(y => y.id === x.id) === ind)
      .map(x => {
        const arr = this.knownJudges.filter(y => y.id === x.id && y.inactive === false);
        const inactive = arr.length == 0;
        return Object.assign({}, x, { inactive });
      });
  }

  isJudgeInactive(judge: any): boolean {
    const fiveMinutes = 5 * 60 * 1000;
    const inactive = (this.currentTime - judge.lastSeen) > fiveMinutes;
    if (inactive) {
      judge.inactive = true;
    }

    const res = judge.inactive || inactive;
    return res;
  }

  calcInactive(judge: any) {
    const res = this.isJudgeInactive(judge);

    return { inactive: res };
  }

  dynamicStyleClass(hide: boolean) {
    if (this.loggedInStation) {
      return {
        hidemefast: hide,
        showmefast: !hide
      };
    } else {
      return {
        hidemefast: !hide,
        showmefast: hide
      };
    }

  }

  displaySelectedGroupName(group): string {
    if (group == -1) {
      return 'allen Gruppen';
    }
    return `Gruppe ${this.gradingGroups[group].title}`;
  }


  onParticipantChange(value: ParticipantPartGroupResult) {
    const grade: GradeDto = {
      uuid: this.gradeService.createUuid(),
      judgeId: this.userId,
      participantId: value.participantId,
      competitionId: this.id,
      gradingGroupId: value.gradingGroupId,
      stationId: value.result.stationId,
      grade: JSON.stringify({ grades: value.result.variables })
    };

    this.pendingMessages.push(grade);
    console.log('SENDING GRADE: ');
    console.log(grade);

    this.gradeService.enterGrade(grade, this.uniqueStations[this.selectedStation].displayName);

    /*const participantResult = this.results.find(part => part.participantId === value.participantId);

    if (participantResult) {
      const gradingGroup = participantResult.gradingGroups.find(gr => gr.gradingGroupId);
      if (gradingGroup) {
        gradingGroup.stations = value.results;
      } else {
        participantResult.gradingGroups.push({
          gradingGroupId: value.gradingGroupId,
          stations: value.results
        });
      }
    } else {
      this.results.push({
        participantId: value.participantId,
        gradingGroups: [
          {
            gradingGroupId: value.gradingGroupId,
            stations: value.results
          }
        ]
      });
    }
    console.log('result::', this.results); */
  }

  finishGrading() {
    const clone = JSON.parse(JSON.stringify(this.results)) as ParticipantResultDTO[];
    clone.forEach(result => result.gradingGroups = JSON.stringify(result.gradingGroups));
    this.service.sendJudgingsForTournament(this.id, clone).subscribe({
      next: data => {
        this.toastr.success('Judgings successfully saved!');
        this.router.navigate(['/competition/' + this.id]);
      },
      error: error => {
        this.toastr.error(error, 'Error fetching grading and registration data');
      }
    });
  }

  getOtherJudges(gradingGroup: any) {
    return this.knownJudges.filter(j => j.info.id != this.userId && j.info.belongsTo.gradingGroupId == gradingGroup.id);
  }

  judgeInGroup(j: any, judgeInfo: any): boolean {
    return j.info.id == judgeInfo.id
      && j.info.belongsTo.gradingGroupId == judgeInfo.belongsTo.gradingGroupId;
  }

  subscribeToWebsocketEvents() {
    this.gradeService.initialize();
    this.gradeService.judgeEvent$.subscribe((judgeInfo => {
      const judge = judgeInfo.id;
      if (judgeInfo.type === InfoType.goodbye) {
        setTimeout(
          () => {
            if (this.knownJudges.filter(j => this.judgeInGroup(j, judgeInfo)).length === 1) {
              this.knownJudges[
                this.knownJudges
                  .findIndex(j => this.judgeInGroup(j, judgeInfo))
              ].inactive = true;
            }
          },
          1000);
      } else if (this.knownJudges.filter(j => this.judgeInGroup(j, judgeInfo)).length !== 1) {
        const ind = this.knownJudges.push({ info: judgeInfo }) - 1;
        this.userService.getUserDetail(judge).subscribe({
          next: (val) => {
            this.knownJudges[ind] = Object.assign({}, val, { inactive: false, lastSeen: Date.now(), info: judgeInfo });
            setTimeout(() => this.gradeService.sendJudgeGreeting(
              judgeInfo.belongsTo.competitionId,
              judgeInfo.belongsTo.gradingGroupId,
              judgeInfo.belongsTo.stationId),
              5000);
          },
          error: (err) => {
            this.toastr.error(err);
          }
        });
      } else {
        const kJ = this.knownJudges[this.knownJudges.findIndex(j => this.judgeInGroup(j, judgeInfo))];
        if (kJ.inactive) {
          setTimeout(() => this.gradeService.sendJudgeGreeting(
            judgeInfo.belongsTo.competitionId,
            judgeInfo.belongsTo.gradingGroupId,
            judgeInfo.belongsTo.stationId),
            5000);
        }
        kJ.inactive = false;
        kJ.lastSeen = Date.now();
      }
    }).bind(this));

    this.gradeService.messageErrors$.subscribe(error => {
      //TODO - differentiate between error types
      this.toastr.error(error.message);
      const existing = this.pendingMessages.findIndex(g => g.uuid == error.uuid);

      if (existing !== -1) {
        const grade = this.pendingMessages.splice(existing, 1)[0] as any;
        grade.hasError = true;
        grade.errorMessage = error.message;
        this.setGrade(grade);
        this.onFilterInputChanged();
      } else {
        this.toastr.error('We don\'t know the origin of this error oO');
        console.log('We don\'t know the origin of this error oO');
      }
    });

    this.gradeService.gradeEvent$.subscribe(grade => {
      const hasPending = this.pendingMessages.findIndex(g => g.uuid == grade.uuid);
      if (hasPending !== -1) {
        this.pendingMessages.splice(hasPending, 1);
      } else {
        if (grade.result !== undefined && grade.result !== null && isFinite(grade.result)) {
          const g = this.gradingGroups.find(x => x.id == grade.gradingGroupId);
          if (g === null) {
            return;
          }

          const reg = g.registrations.find(r => r.participant.id == grade.participantId);
          if (reg === null) {
            return;
          }

          const station = reg.stations.find(s => s.id == grade.stationId);
          if (station === null) {
            return;
          }

          const gs = station.grades.map(gd => {
            gd.result = grade.result;
            gd.uuid = null;
            return gd;
          });

          for (const element of gs) {
            if (element.judgeId == grade.judgeId) {
              continue;
            }
            this.setGrade(element);
          }
        }
      }
      this.setGrade(grade);
      this.onFilterInputChanged();
    });
  }
}
