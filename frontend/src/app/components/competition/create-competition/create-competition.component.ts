import {ChangeDetectorRef, Component, ElementRef, OnInit, QueryList, ViewChildren} from '@angular/core';
import {AbstractControl, FormControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {CompetitionService} from '../../../services/competition.service';
import {GradingSystemService} from '../../../services/grading-system.service';
import {CompetitionDetail} from '../../../dtos/competition-detail';
import {ListError} from '../../../dtos/list-error';
import {cloneDeep} from 'lodash';
import {ToastrService} from 'ngx-toastr';
import {UserDetail} from '../../../dtos/user-detail';
import {of} from 'rxjs';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';
import {TemplateAction, TemplateState} from 'src/app/datatypes/templateAction';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {TemplateDialogComponent} from '../template-dialog/template-dialog.component';
import {CreateCompetitionSelectGradingSystemDialogComponent}
  from '../../create-competition-select-grading-system-dialog/create-competition-select-grading-system-dialog.component';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {HttpResponse} from '@angular/common/http';
import {Competition} from '../../../dtos/competition';
import { stat } from 'fs';
import { DownloadReportDialogComponent } from '../../download-report-dialog/download-report-dialog.component';
import { Globals } from 'src/app/global/globals';

@Component({
  selector: 'app-create-competition',
  templateUrl: './create-competition.component.html',
  styleUrls: ['./create-competition.component.scss']
})
export class CreateCompetitionComponent implements OnInit {

  id: number = null;
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
  selectedFile: File;
  imageUrl: string | SafeUrl = '../../../assets/turnier.jpg';
  competitionPictureDummy: Competition;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private toastr: ToastrService,
    private changeDetectorRef: ChangeDetectorRef,
    private competitionService: CompetitionService,
    private gradingSystemService: GradingSystemService,
    private userService: UserService,
    private globals: Globals,
    public dialog: MatDialog,
    private sanitizer: DomSanitizer) {

    this.competitionForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      description: ['', []],
      email: ['', [Validators.email]],
      phone: ['', [Validators.pattern('^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$')]],
      beginOfRegistration: ['', [Validators.required]],
      endOfRegistration: ['', [Validators.required]],
      beginOfCompetition: ['', [Validators.required]],
      endOfCompetition: ['', [Validators.required]],
      public: ['FALSE', [Validators.required]],
      draft: ['TRUE', [Validators.required]],
    });
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.id = parseInt(params.id, 10);

        this.competitionService.getCompetitionByIdDetail(this.id).subscribe({
          next: data => {
            this.competitionForm.controls['name'].setValue(data.name);
            this.competitionForm.controls['description'].setValue(data.description);
            this.competitionForm.controls['email'].setValue(data.email);
            this.competitionForm.controls['phone'].setValue(data.phone);
            this.competitionForm.controls['beginOfRegistration'].setValue(this.formatDate(data.beginOfRegistration));
            this.competitionForm.controls['endOfRegistration'].setValue(this.formatDate(data.endOfRegistration));
            this.competitionForm.controls['beginOfCompetition'].setValue(this.formatDate(data.beginOfCompetition));
            this.competitionForm.controls['endOfCompetition'].setValue(this.formatDate(data.endOfCompetition));
            this.competitionForm.controls['public'].setValue(data.public ? 'TRUE' : 'FALSE');
            this.competitionForm.controls['draft'].setValue(data.draft ? 'TRUE' : 'FALSE');

            this.competitionService.getCompetitionById(params.id).subscribe({
              next: compPictureData => {
                  this.competitionPictureDummy = compPictureData;
                  if (this.competitionPictureDummy.picturePath) {
                    this.imageUrl = this.globals.backendOrigin + this.competitionPictureDummy.picturePath;
                    this.getImageFile(this.imageUrl.toString());
                    console.log(this.imageUrl);
                  }
              },
              error: error => {
                this.toastr.error(error, 'Error fetching competition information');
              }
            });

            if (data.gradingGroups !== null && data.gradingGroups !== undefined) {
              this.gradingGroups = data.gradingGroups;

              for (const gradingGroup of this.gradingGroups) {
                const gradingSystem = JSON.parse(gradingGroup.gradingSystemDto.formula);
                gradingGroup.stations = gradingSystem.stations.map(station => ({
                  id: station.id,
                  idCount: Math.max(...station.variables.map(s => s.id)),
                  minJudgeCount: station.variables.length > 0 ? station.variables[0].minJudgeCount : 1,
                  title: station.displayName,
                  variables: station.variables.map(variable => ({
                    name: variable.displayName,
                    type: 'variable',
                    typeHint: 'variableRef',
                    strategy: variable.strategy,
                    value: variable.id
                  })),
                  constants: this.parseConstantsFromFormular(station.formula)
                                .filter((val, ind, array) => array.findIndex(va => va.value === val.value) === ind),
                  selectedVariable: {name: '', value: -1, strategy: 'mean'},
                  formula: {
                    valid: true,
                    data: station.formula
                  }
                }));
                gradingGroup.formula = ({
                  valid: true,
                  data: gradingSystem.formula
                });
                gradingGroup.templateState = TemplateState.none;
                gradingGroup.constants = this.parseConstantsFromFormular(gradingSystem.formula)
                  .filter((val, ind, array) => array.findIndex(va => va.value === val.value) === ind);

                gradingGroup.idCount = Math.max(...gradingSystem.stations.map(s => s.id));
                gradingGroup.stationVariables = gradingSystem.stations.map(station => ({
                  name: station.displayName,
                  value: station.id,
                  type: 'variable',
                  typeHint: 'variableRef'
                }));
              }
            }

            if (data.judges !== null && data.judges !== undefined) {
              this.judges = data.judges;
            }
          },
          error: error => {
            console.error('Error fetching competition information', error);
            this.toastr.error('Der Wettkampf den Sie bearbeiten wollen wurde nicht gefunden.', 'Wettkampf nicht gefunden');
          }
        });
      }
    });
  }

  getImageFile(path: string): Promise<File> {
    return new Promise((resolve, reject) => {
      fetch(path)
        .then(response => response.blob())
        .then(blob => {
          const extension = path.split('.').pop();
          let mimeType = '';
          if (extension === 'jpg' || extension === 'jpeg') {
            mimeType = 'image/jpeg';
          } else if (extension === 'png') {
            mimeType = 'image/png';
          }
          const fileReader = new FileReader();
          fileReader.onload = (event: any) => {
            this.selectedFile = new File([event.target.result], 'imageName.' + extension, {type: mimeType});
            resolve(this.selectedFile);
          };
          fileReader.readAsArrayBuffer(blob);
          console.log('File loaded: ' + this.selectedFile);
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  parseConstantsFromFormular(formula: any): any[] {
    if(['add', 'mult', 'div', 'subt'].includes(formula.typeHint)) {
      return [...this.parseConstantsFromFormular(formula.left), ...this.parseConstantsFromFormular(formula.right)];
    } else if (formula.typeHint ==='const') {
      return [formula];
    } else {
      return [];
    }
  }

  checkGradingGroupValid(group): any[] {
    const invalidErrors = [];

    if (!group.formula.valid) {
      invalidErrors.push(group.title);
    }
    if (this.gradingGroups.filter(g => g.title.trim() === group.title.trim()).length > 1) {
      invalidErrors.push(`${group.title} ist kein einzigartiger Name`);
    }

    if (group.stations.length > 0) {
      for (const station of group.stations) {
        if (!station.formula.valid) {
          invalidErrors.push(`${group.title} -> ${station.title}`);
        }
        if (group.stations.filter(s => s.title.trim() === station.title.trim()).length > 1) {
          invalidErrors.push(`${group.title} -> ${station.title} ist kein einzigartiger Name`);
        }

        if (station.variables.length > 0) {
          for (const variable of station.variables) {
            if (station.variables.filter(v => v.name.trim() === variable.name.trim()).length > 1) {
              invalidErrors.push(`${group.title} -> ${station.title} -> ${variable.name} ist kein einzigartiger Name`);
            }
          }
        }
      }
    }
    return invalidErrors;
  }

  createCompetition() {
    this.submitted = true;
    if (!this.competitionForm.valid) {
      this.toastr.error('Bitte füllen Sie alle benötigten Felder aus', 'Unvollständige Angaben');
      return;
    }

    if (this.selectedFile && this.selectedFile.size > 1000000) {
      this.toastr.error('Datei größer als 1 Megabyte', 'Datei zu groß');
      return;
    }

    if (this.gradingGroups.length !== 0) {
      const invalidErrors = [];

      for (const group of this.gradingGroups) {
        invalidErrors.push(...this.checkGradingGroupValid(group));
      }

      if (invalidErrors.length !== 0) {
        this.toastr.error(
          `Das Turnier kann nicht gespeichert werden da folgende Formeln Probleme aufweisen:
<ul>
${invalidErrors.map(e => '<li>' + e + '</li>').join('\n')}`,
          `Ungültige Formeln`, {
            enableHtml: true
          });

        return;
      }
    }

    const competition = new CompetitionDetail();
    Object.assign(competition, this.competitionForm.value);
    if (this.id !== null) {
      competition.id = this.id;
    }
    if (competition.phone === '') {
      competition.phone = null;
    }
    if (competition.email === '') {
      competition.email = null;
    }
    if (competition.description === '') {
      competition.description = null;
    }

    for (const gradingGroup of this.gradingGroups) {
      const gradingSystem = {
        name: 'default',
        description: 'default',
        isPublic: false,
        isTemplate: false,
        formula: JSON.stringify({
          stations: gradingGroup.stations.map((station, stationId) => ({
            displayName: station.title,
            id: station.id,
            variables: station.variables.map(variable => ({
              displayName: variable.name,
              strategy: variable.strategy,
              minJudgeCount: station.minJudgeCount,
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
          if (this.id !== null) {
            this.toastr.success('Turnier erfolgreich bearbeitet!');
            if (this.selectedFile !== undefined && this.selectedFile !== null) {
              this.uploadFile(value.id);
            } else {
              this.router.navigate(['/competition', this.id]);
            }
          } else {
            this.toastr.success('Turnier erfolgreich erstellt!');
            if (this.selectedFile !== undefined && this.selectedFile !== null) {
              this.uploadFile(value.id);
            } else {
              console.log(value);
              this.router.navigate(['/competition', value.id]);
            }
          }
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
      constants: [],
      formula: {valid: false, data: {}},
      idCount: 0,
      templateState: TemplateState.none
    });
  }

  openImportGroupDialog() {
    this.gradingSystemService.getSimpleDraftGradingSystems().subscribe({
      next: data => {
        const dialogConfig = new MatDialogConfig();

        dialogConfig.data = data;

        const dialogRef = this.dialog.open(CreateCompetitionSelectGradingSystemDialogComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
          if (!result.selectedGradingSystemId) {
            return;
          }

          this.fetchAndAddGroupById(result.selectedGradingSystemId);
        });

      }
    });
  }

  fetchAndAddGroupById(id: number): void {
    this.gradingSystemService.getDraftGradingSystemById(id).subscribe({
      next: data => {
        const gradingSystem = JSON.parse(data.formula);
        const newGradingGroup: any = {
          title: data.name,
          idCount: Math.max(...gradingSystem.stations.map(s => s.id)),
          templateState: TemplateState.none,
        };
        newGradingGroup.stations = gradingSystem.stations.map(station => ({
          title: station.displayName,
          id: station.id,
          minJudgeCount: station.variables.length > 0 ? station.variables[0].minJudgeCount : 1,
          selectedVariable: {name: '', value: -1, strategy: 'mean'},
          variables: station.variables.map(variable => ({
            name: variable.displayName,
            type: 'variable',
            typeHint: 'variableRef',
            strategy: variable.strategy,
            value: variable.id
          })),
          formula: {
            valid: true,
            data: station.formula
          },
          idCount: Math.max(...station.variables.map(v => v.id))
        }));
        newGradingGroup.formula = ({
          valid: true,
          data: gradingSystem.formula
        });
        newGradingGroup.stationVariables = gradingSystem.stations.map(station => ({
          name: station.displayName,
          value: station.id,
          type: 'variable',
          typeHint: 'variableRef'
        }));

        newGradingGroup.constants = [];

        this.gradingGroups.push(newGradingGroup);
      }
    });
  }

  addStation(id) {
    this.gradingGroups[id].stations.push({
      title: `Station ${++this.gradingGroups[id].idCount}`,
      id: this.gradingGroups[id].idCount,
      minJudgeCount: 1,
      selectedVariable: {name: '', value: -1, strategy: 'mean'},
      constants: [],
      variables: [], idCount: 0, formula: {valid: false, data: {}}
    });

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

  editVar(station, variable) {
    station.selectedVariable = {
      name: variable.name,
      strategy: variable.strategy.type,
      value: variable.value
    };

  }

  clearVariable(station) {
    station.selectedVariable = {
      name: '',
      strategy: 'mean',
      value: -1
    };
  }

  addStationVariable(station) {

    if(station.selectedVariable.name.trim() === '') {
      return;
    }

    if (station.selectedVariable.name.length > 16) {
      this.toastr.error('Variablennamen dürfen maximal 16 Zeichen lang sein!', 'Achtung:');
      station.selectedVariable.name = station.selectedVariable.name.substr(0, 16);
      return;
    }

    if(station.selectedVariable.value !== -1) {
      const existing = station.variables.find(v => v.value === station.selectedVariable.value);

      if(!existing) {
        this.toastr.error('The world is going to blow up', 'Eins Error');
        return;
      }

      if(station.variables.find(va => va.name === station.selectedVariable.name.trim() && va.value !== existing.value) !== undefined) {
        this.toastr.warning('Variablennamen müssen innerhalb von einer Station einzigartig sein', 'Einzigartiger Name');
        return;
      }

      this.toastr.info(`${existing.name} erfolgreich geändert`);

      existing.name = station.selectedVariable.name;
      existing.strategy = {
        type: station.selectedVariable.strategy
      };
    } else {
      if(station.variables.find(va => va.name === station.selectedVariable.name.trim()) !== undefined) {
        this.toastr.warning('Variablennamen müssen innerhalb von einer Station einzigartig sein', 'Einzigartiger Name');
        return;
      }

      station.variables.push({
        name: station.selectedVariable.name.trim(),
        value: ++station.idCount,
        strategy: {
          type: station.selectedVariable.strategy
        },
        type: 'variable',
        typeHint: 'variableRef',
        spaces: 0,
        priority: 0
      });
    }

    station.selectedVariable = {
      name: '',
      strategy: 'mean',
      value: -1
    };


    station.variables = cloneDeep(station.variables);
  }

  updateGroupName(id, newName) {
    if (newName.length > 16) {
      this.toastr.error('Gruppennamen dürfen maximal 16 Zeichen lang sein!', 'Achtung:');
      newName = newName.substr(0, 16);
    }
    if (this.gradingGroups.map(g => g.title.trim()).includes(newName.trim())) {
      this.toastr.warning('Gruppen müssen einzigartige Namen haben!', 'Achtung:');
    }
    this.gradingGroups[id].title = newName;
  }

  upadteStationName(groupId, stationId, newName) {
    if (newName.length > 16) {
      this.toastr.error('Stationsnamen dürfen maximal 16 Zeichen lang sein!', 'Achtung:');
      newName = newName.substr(0, 16);
    }
    if (this.gradingGroups[groupId].stations.map(s => s.title.trim()).includes(newName.trim())) {
      this.toastr.warning('Stationsnamen müssen innerhalb einer Gruppen einzigartig sein!', 'Achtung:');
    }

    this.gradingGroups[groupId].stations[stationId].title = newName;
    this.gradingGroups[groupId].stationVariables[stationId].name = newName;

    this.gradingGroups[groupId].stationVariables = cloneDeep(this.gradingGroups[groupId].stationVariables);
  }

  change(station, newName) {
    station.variables = cloneDeep(station.variables.filter(x => x.name !== ''));

    if (station.variables.filter(v => v.name.trim() === newName.trim()).length > 1) {
      this.toastr.warning('Variablennamen müssen innerhalb einer Station einzigartig sein!', 'Achtung:');
    }
  }

  duplicateGroup(group, id) {
    if (!this.gradingGroups[id].formula.valid) {

      this.toastr.error(
        `${this.gradingGroups[id].title} kann nicht dupliziert werden da die Formel ungültig ist.`
        , 'Fehler beim Duplizieren');
      return;
    }

    if (this.gradingGroups[id].stations.length > 0
      && this.gradingGroups[id].stations.filter(s => !s.formula.valid).length > 0) {
      this.toastr.error(
        `${this.gradingGroups[id].title} kann nicht dupliziert werden da sie ungültige Formeln enthält.`
        , 'Fehler beim Duplizieren');
      return;
    }

    this.gradingGroups.splice(id + 1, 0, cloneDeep(Object.assign({}, group, {title: group.title + ' Kopie'})));
    this.toastr.info(`${this.gradingGroups[id + 1].title} erfolgreich erstellt.`);
  }

  duplicateStation(groupId, station, stationId) {
    if (!this.gradingGroups[groupId].stations[stationId].formula.valid) {
      this.toastr.error(
        `${this.gradingGroups[groupId].stations[stationId].title} kann nicht dupliziert werden da die Formel ungültig ist`
        , 'Fehler beim Duplizieren');
      return;
    }
    this.gradingGroups[groupId].stations.splice(stationId + 1, 0, cloneDeep(
      Object.assign({}, station, {title: station.title + ' Kopie', id: ++this.gradingGroups[groupId].idCount})));

    this.gradingGroups[groupId].stationVariables.splice(stationId + 1, 0, {
      name: this.gradingGroups[groupId].stations[stationId + 1].title,
      value: this.gradingGroups[groupId].idCount,
      type: 'variable',
      typeHint: 'variableRef',
      spaces: 0,
      priority: 0
    });
    this.gradingGroups[groupId].stationVariables = cloneDeep(this.gradingGroups[groupId].stationVariables);
    this.toastr.info(`${this.gradingGroups[groupId].stations[stationId + 1].title} erfolgreich erstellt.`);
  }

  deleteGroup(id) {
    this.gradingGroups.splice(id, 1);
  }

  deleteStation(groupId, stationId) {
    this.gradingGroups[groupId].stations.splice(stationId, 1);

    this.gradingGroups[groupId].stationVariables.splice(stationId, 1);
    this.gradingGroups[groupId].stationVariables = cloneDeep(this.gradingGroups[groupId].stationVariables);
    console.log(this.gradingGroups);
  }

  handleTemplateAction(id, action: TemplateAction) {
    const invalidErrors = this.checkGradingGroupValid(this.gradingGroups[id]);

    if (invalidErrors.length !== 0) {
      this.toastr.error(
        `${this.gradingGroups[id].title} kann nicht als Vorlage gespeichert werden da folgende Formeln Probleme aufweisen:
<ul>
${invalidErrors.map(e => '<li>' + e + '</li>').join('\n')}`,
        `Ungültige Formeln`, {
          enableHtml: true
        });

      return;
    }

    const gradingSystem = {
      name: this.gradingGroups[id].title,
      description: this.gradingGroups[id].description,
      isPublic: false,
      isTemplate: true,
      formula: JSON.stringify({
        stations: this.gradingGroups[id].stations.map((station, stationId) => ({
          displayName: station.title,
          id: station.id,
          variables: station.variables.map(variable => ({
            displayName: variable.name,
            strategy: variable.strategy,
            minJudgeCount: station.minJudgeCount,
            id: variable.value
          })),
          formula: station.formula.data
        })),
        formula: this.gradingGroups[id].formula.data
      })
    };

    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {gradingSystem};

    const dialogRef = this.dialog.open(TemplateDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (!result.save) {
        return;
      }

      this.gradingGroups[id].templateState = result.value;
    });
  }

  public dynamicCssClassesForInput(input: AbstractControl): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': (!input.valid && !input.pristine) || (this.submitted && input.pristine && input.invalid),
    };
  }

  public formatDate(date: Date): string {
    if (date === null || date === undefined) {
      return '';
    }
    return date.toISOString().split('.')[0];
  }

  formatJudge(judge: UserDetail) {
    if (judge === undefined || judge === null) {
      return '';
    }

    return judge.firstName + ' ' + judge.lastName + ' (' +
      judge.dateOfBirth.toLocaleDateString() + ')';
  }

  judgeSuggestions = (input: string) => (input === '')
    ? of([])
    : this.userService.searchByName(input, 5);

  addJudge() {
    if (this.currentJudge === undefined) {
      this.error = true;
      this.errMsg.message = 'Kein Wettkampfrichter ausgewählt';
      return;
    }

    if (this.judges.filter((p) => this.currentJudge.id === p.id).length > 0) {
      this.error = true;
      this.toastr.error('Wettkampfrichter bereits ausgewählt');
      return;
    }

    this.judges.push(this.currentJudge);
  }

  removeJudge(index: number) {
    this.judges.splice(index, 1);
  }

  selectFile(event) {
    this.selectedFile = event.target.files[0];
    this.imageUrl = this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(event.target.files[0]));
  }

  uploadFile(id: number) {
    if (this.selectedFile != null) {
      const extension = this.selectedFile.name.substring(this.selectedFile.name.lastIndexOf('.'));
      if (extension === '.png' || extension === '.jpeg' || extension === '.jpg') {
        this.competitionService.uploadPicture(id, this.selectedFile).subscribe(
          event => {
            if (event instanceof HttpResponse) {
              this.router.navigate(['/competition', id]);
            }
          },
          (error) => {
            this.toastr.error(error.error);
          }, () => {
          });
      } else {
        this.toastr.error('Falscher Dateityp.');
      }
    } else {
      this.toastr.error('Eine Datei muss ausgewählt sein.');
    }
  }

  fileSelected() {
    return !(this.selectedFile == null || false);
  }

  clearFileSelection() {
    this.selectedFile = null;
    this.imageUrl = '../../../assets/turnier.jpg';
  }
}
