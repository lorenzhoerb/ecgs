import {ChangeDetectorRef, Component, ElementRef, OnInit, QueryList, ViewChildren} from '@angular/core';
import {AbstractControl, FormControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {CompetitionService} from '../../../services/competition.service';
import {CompetitionDetail} from '../../../dtos/competition-detail';
import {ListError} from '../../../dtos/list-error';
import { GradingGroupDetail } from 'src/app/dtos/gradingGroupDetail';
import { cloneDeep } from 'lodash';
import { ToastrService } from 'ngx-toastr';
import {UserDetail} from '../../../dtos/user-detail';
import {of} from 'rxjs';

@Component({
  selector: 'app-create-competition',
  templateUrl: './create-competition.component.html',
  styleUrls: ['./create-competition.component.scss']
})
export class CreateCompetitionComponent implements OnInit {

  @ViewChildren('inputVariables') inputVariables: QueryList<ElementRef>;

  gradingGroups: any[] = [];
  judges: UserDetail[] = [];
  currentJudge: UserDetail;

  ids: any[] = [];

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
    private toastr: ToastrService,
    private changeDetectorRef: ChangeDetectorRef,
    private competitionService: CompetitionService,
    private userService: UserService) {
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
      this.toastr.error('Bitte füllen Sie alle benötigten Felder aus', 'Unvollständige Angaben');
      return;
    }

    if(this.gradingGroups.length !== 0) {
      const invalidErrors = [];

      for (const group of this.gradingGroups) {
        if(!group.formula.valid) {
          invalidErrors.push(group.title);
        }
        if(this.gradingGroups.filter(g => g.title.trim() === group.title.trim()).length > 1) {
          invalidErrors.push(`${group.title} ist kein einzigartiger Name`);
        }

        if(group.stations.length > 0) {
          for (const station of group.stations) {
            if(!station.formula.valid) {
              invalidErrors.push(`${group.title} -> ${station.title}`);
            }
            if(group.stations.filter(s => s.title.trim() === station.title.trim()).length > 1) {
              invalidErrors.push(`${group.title} -> ${station.title} ist kein einzigartiger Name`);
            }

            if(station.variables.length > 0) {
              for(const variable of station.variables) {
                if(station.variables.filter(v => v.name.trim() === variable.name.trim()).length > 1) {
                  invalidErrors.push(`${group.title} -> ${station.title} -> ${variable.name} ist kein einzigartiger Name`);
                }
              }
            }
          }
        }
      }

      if(invalidErrors.length !== 0) {
        this.toastr.error(
          `Das Turnier kann nicht gespeichert werden da folgende Formeln Probleme aufweisen:
<ul>
${invalidErrors.map(e => '<li>' + e + '</li>').join('\n')}`,
          `Ungültige Formeln`,{
            enableHtml :  true
          });

        return;
      }
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

    for(const gradingGroup of this.gradingGroups) {
      const gradingSystem = {
        name: 'default',
        description: 'default',
        isPublic: false,
        formula: JSON.stringify({
          stations: gradingGroup.stations.map((station, stationId) => ({
            displayName: station.title,
            id: stationId + 1,
            variables: station.variables.map(variable => ({
              displayName: variable.name,
              id: variable.value
            })),
            formula: station.formula.data
          })),
          formula: gradingGroup.formula.data
        })
      };
      gradingGroup.gradingSystemDto = gradingSystem;
    }



    competition.gradingGroups = this.gradingGroups;
    competition.judges = this.judges;

    this.competitionService.createCompetition(competition)
      .subscribe({
        next: value => {
          this.toastr.success('Turnier erfolgreich erstellt!');
          this.router.navigate(['/competition',value.id]);
          //ToDO: Integrate with dashboard
        },
        error: err => {
          console.log(err.error.errors);
          this.toastr.error(
            `<ul>${err.error.errors.map(e => '<li>' + e + '</li>').join('\n')}</ul>`,
            err.error.message,
            {enableHtml: true});
        }
      });
  }

  public vanishError() {
    this.error = false;
  }

  addGroup() {
    this.gradingGroups.push({
      title: `Gruppe ${this.gradingGroups.length + 1}`,
      stations: [],
      stationVariables: [],
      formula: {valid: false, data: {} },
      idCount: 0
    });
  }

  addStation(id) {
    this.gradingGroups[id].stations.push({title: `Station ${++this.gradingGroups[id].idCount}`,
      variables: [], idCount: 0, formula: {valid: false, data: {} }});

    this.gradingGroups[id].stationVariables.push({
      name: `Station ${this.gradingGroups[id].idCount}`,
      value: this.gradingGroups[id].idCount,
      type: 'variable',
      typeHint: 'variableRef',
      spaces: 0,
      priority: 0
    });

    this.gradingGroups[id].stationVariables = cloneDeep(this.gradingGroups[id].stationVariables);
  }

  addStationVariable(station) {
    station.variables.push({
      name: '',
      value: ++station.idCount,
      type: 'variable',
      typeHint: 'variableRef',
      spaces: 0,
      priority: 0});

    station.variables = cloneDeep(station.variables);

    this.changeDetectorRef.detectChanges();
    this.inputVariables.last.nativeElement.focus();
  }

  updateGroupName(id, newName) {
    if(this.gradingGroups.map(g => g.title.trim()).includes(newName.trim())) {
      this.toastr.warning('Gruppen müssen einzigartige Namen haben!', 'Achtung:');
    }
    this.gradingGroups[id].title = newName;
  }

  upadteStationName(groupId, stationId, newName) {
    if(this.gradingGroups[groupId].stations.map(s => s.title.trim()).includes(newName.trim())) {
      this.toastr.warning('Stationsnamen müssen innerhalb einer Gruppen einzigartig sein!', 'Achtung:');
    }

    this.gradingGroups[groupId].stations[stationId].title = newName;
    this.gradingGroups[groupId].stationVariables[stationId].name = newName;

    this.gradingGroups[groupId].stationVariables = cloneDeep(this.gradingGroups[groupId].stationVariables);
  }

  change(station, newName) {
    station.variables = cloneDeep(station.variables.filter(x => x.name !== ''));

    if(station.variables.filter(v => v.name.trim() === newName.trim()).length > 1) {
      this.toastr.warning('Variablennamen müssen innerhalb einer Station einzigartig sein!', 'Achtung:');
    }
  }

  duplicateGroup(group,id) {
    if(!this.gradingGroups[id].formula.valid) {

      this.toastr.error(
        `${this.gradingGroups[id].title} kann nicht dupliziert werden da die Formel ungültig ist.`
        ,'Fehler beim Duplizieren');
      return;
    }

    if(this.gradingGroups[id].stations.length > 0
      && this.gradingGroups[id].stations.filter(s => !s.formula.valid).length > 0) {
        this.toastr.error(
          `${this.gradingGroups[id].title} kann nicht dupliziert werden da sie ungültige Formeln enthält.`
          ,'Fehler beim Duplizieren');
        return;
      }

    this.gradingGroups.splice(id+1, 0, cloneDeep(Object.assign({}, group, {title: group.title + ' Kopie'})));
    this.toastr.info(`${this.gradingGroups[id+1].title} erfolgreich erstellt.`);
  }

  duplicateStation(groupId, station, stationId) {
    if(!this.gradingGroups[groupId].stations[stationId].formula.valid) {
      this.toastr.error(
        `${this.gradingGroups[groupId].stations[stationId].title} kann nicht dupliziert werden da die Formel ungültig ist`
        ,'Fehler beim Duplizieren');
      return;
    }
    this.gradingGroups[groupId].stations.splice(stationId+1, 0, cloneDeep(
      Object.assign({}, station, {title: station.title + ' Kopie', value: ++this.gradingGroups[groupId].idCount})));

    this.gradingGroups[groupId].stationVariables.push({
      name: this.gradingGroups[groupId].stations[stationId+1].title,
      value: this.gradingGroups[groupId].idCount,
      type: 'variable',
      typeHint: 'variableRef',
      spaces: 0,
      priority: 0
    });
    this.gradingGroups[groupId].stationVariables = cloneDeep(this.gradingGroups[groupId].stationVariables);
    this.toastr.info(`${this.gradingGroups[groupId].stations[stationId+1].title} erfolgreich erstellt.`);
  }

  deleteGroup(id) {
    this.gradingGroups.splice(id, 1);
  }

  deleteStation(groupId, stationId) {
    this.gradingGroups[groupId].stations.splice(stationId, 1);

    this.gradingGroups[groupId].stationVariables.splice(stationId, 1);
    this.gradingGroups[groupId].stationVariables = cloneDeep(this.gradingGroups[groupId].stationVariables);
  }

  public dynamicCssClassesForInput(input: AbstractControl): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': (!input.valid && !input.pristine) || (this.submitted && input.pristine && input.invalid),
    };
  }

  formatJudge(judge: UserDetail) {
    if(judge === undefined || judge === null) {
      return '';
    }

    return judge.firstName + ' ' + judge.lastName + ' (' +
      judge.dateOfBirth.toLocaleDateString() + ')';
  }

  judgeSuggestions = (input: string) => (input === '')
    ? of([])
    : this.userService.searchByName(input, 5);

  addJudge() {
    if(this.currentJudge === undefined) {
      this.error = true;
      this.errMsg.message = 'Kein Wettkampfrichter ausgewählt';
      return;
    }

    if(this.judges.filter((p) => this.currentJudge.id === p.id).length > 0) {
      this.error = true;
      this.toastr.error('Wettkampfrichter bereits ausgewählt');
      return;
    }

    this.judges.push(this.currentJudge);
  }

  removeJudge(index: number) {
    this.judges.splice(index, 1);
  }
}
