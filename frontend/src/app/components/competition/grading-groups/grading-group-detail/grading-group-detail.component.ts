import {Component, LOCALE_ID, OnInit} from '@angular/core';
import {CONDITIONS} from './form-conditions';
import {FormBuilder, FormControl, FormGroup, FormArray, Validators} from '@angular/forms';
import {GradingGroupService} from '../../../../services/grading-group.service';
import {RegisterConstraint} from '../../../../dtos/register-constraint';
import {CompetitionService} from '../../../../services/competition.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DetailedGradingGroupDto} from '../../../../dtos/detailed-grading-group-dto';
import {ToastrService} from 'ngx-toastr';
import {DatePipe} from '@angular/common';
import {notEmptyValidator} from '../condition-filter-input/non-empty-condition-value-validator';
import LocalizationService, {LocalizeService} from '../../../../services/localization/localization.service';

@Component({
  selector: 'app-grading-group-detail',
  templateUrl: './grading-group-detail.component.html',
  styleUrls: ['./grading-group-detail.component.scss'],
  providers: [
    {provide: LOCALE_ID, useValue: 'en-US'},
    DatePipe
  ]
})
export class GradingGroupDetailComponent implements OnInit {

  conditionMode = 'manual';
  conditionProps = CONDITIONS;
  conditionFormGroup: FormGroup;
  groupId: number;
  gradingGroup: DetailedGradingGroupDto;


  constructor(
    private fb: FormBuilder,
    private gradingGroupService: GradingGroupService,
    private competitionService: CompetitionService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService
  ) {
  }

  get conditionForms() {
    return this.conditionFormGroup.get('conditions') as FormArray;
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.onUrlChange();
    this.conditionFormGroup = this.fb.group({
      conditions: this.fb.array([])
    });
  }


  addCondition() {
    const condition = new FormControl(
      {
        key: 'GENDER',
        operator: 'EQUALS',
        value: 'MALE'
      },
      notEmptyValidator
    );
    this.conditionForms.push(condition);
  }

  addValuedCondition(key: string, operator: string, value: string) {
    const condition = new FormControl({key, operator, value}, notEmptyValidator);
    this.conditionForms.push(condition);
  }

  removeCondition(index: number) {
    this.conditionForms.removeAt(index);
  }

  onSave() {
    const constraints: RegisterConstraint[] = this.conditionFormGroup.value.conditions
      .map(c => ({value: c.value, type: c.key, operator: c.operator}));
    this.updateConstraints(this.groupId, constraints);
  }

  updateConstraints(groupId: number, constraints) {
    this.gradingGroupService.setGradingGroupConstraints(this.groupId, constraints)
      .subscribe({
        next: value => {
          this.toastr.success('Successfully set constraints');
        }, error: err => {
          console.error(err);
          this.toastr.error('Oops, something went wrong!');
        }
      });
  }

  onUrlChange() {
    this.route.params.subscribe(params => {
      if (params.groupId) {
        this.groupId = parseInt(params.groupId, 10);
        if (isNaN(this.groupId)) {
          this.router.navigate(['/']);
        }
        this.fetchGradingGroup();
      }
    });
  }

  onSelectManual() {
    this.updateConstraints(this.groupId, []);
  }

  fetchGradingGroup() {
    this.gradingGroupService.getOneById(this.groupId)
      .subscribe({
        next: value => {
          this.gradingGroup = value;
          this.initConstraints(value.constraints);
        }, error: err => {
          console.error(err);
          this.router.navigate(['/']);
        }
      });
  }

  initConstraints(constraints: RegisterConstraint[]) {
    if (constraints.length === 0) {
      this.conditionMode = 'manual';
      this.addCondition();
    } else {
      this.conditionMode = 'conditioned';
      constraints.forEach(c => {
        this.addValuedCondition(c.type, c.operator, c.value);
      });
    }
  }

  onAddCondition() {
    this.addCondition();
  }
}
