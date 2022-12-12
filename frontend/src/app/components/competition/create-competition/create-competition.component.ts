import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {CompetitionService} from '../../../services/competition.service';
import {CompetitionDetail} from '../../../dtos/competition-detail';
import {ListError} from '../../../dtos/list-error';
import { GradingGroupDetail } from 'src/app/dtos/gradingGroupDetail';

@Component({
  selector: 'app-create-competition',
  templateUrl: './create-competition.component.html',
  styleUrls: ['./create-competition.component.scss']
})
export class CreateCompetitionComponent implements OnInit {

  gradingGroups: GradingGroupDetail[] = [];

  competitionForm: UntypedFormGroup;
  dateNow = new Date();
  error = false;
  errMsg: ListError = {
    message: '',
    errors: null
  };
  submitted = false;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private competitionService: CompetitionService) {
    this.competitionForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      description: ['', []],
      email: ['', [Validators.email]],
      phone: ['', [Validators.pattern('^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$')]],
      beginOfRegistration: ['', [Validators.required]],
      endOfRegistration: ['', [Validators.required]],
      beginOfCompetition: ['', [Validators.required]],
      endOfCompetition: ['', [Validators.required]],
      isPublic: ['FALSE', [Validators.required]],
      draft: ['TRUE', [Validators.required]],
    });
  }

  ngOnInit(): void {
  }

  createCompetition() {
    this.submitted = true;
    if (!this.competitionForm.valid) {
      return;
    }

    const competition = new CompetitionDetail();
    Object.assign(competition, this.competitionForm.value);
    if (competition.phone === '') {
      competition.phone = null;
    }
    if (competition.email === '') {
      competition.email = null;
    }
    if (competition.description === '') {
      competition.description = null;
    }

    competition.gradingGroups = this.gradingGroups;

    this.competitionService.createCompetition(competition)
      .subscribe({
        next: value => {
          alert('Competition successfully created');
          //ToDO: Integrate with dashboard
        },
        error: err => {
          console.log(err.error.errors);
          this.error = true;
          this.errMsg.message = err.error.message;
          this.errMsg.errors = err.error.errors;
        }
      });
  }

  public vanishError() {
    this.error = false;
  }

  addGroup() {
    this.gradingGroups.push({title: `Gruppe ${this.gradingGroups.length + 1}`});
  }

  duplicateGroup(group,id) {
    this.gradingGroups.splice(id, 0, Object.assign({}, group));
  }

  deleteGroup(id) {
    this.gradingGroups.splice(id, 1);
  }

  public dynamicCssClassesForInput(input: AbstractControl): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': (!input.valid && !input.pristine) || (this.submitted && input.pristine && input.invalid),
    };
  }

}
