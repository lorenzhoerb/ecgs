import { ChangeDetectorRef, Component, ElementRef, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { GradingSystemService } from 'src/app/services/grading-system.service';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';
import {clone, cloneDeep} from 'lodash';
import { ViewEditGradingGroup, ViewEditGradingGroupSearch, ViewEditGradingGroupSearchType } from 'src/app/dtos/grading-group-detail';

@Component({
  selector: 'app-view-and-edit-grading-system',
  templateUrl: './view-and-edit-grading-system.component.html',
  styleUrls: ['./view-and-edit-grading-system.component.scss']
})
export class ViewAndEditGradingSystemComponent implements OnInit {
  @ViewChildren('inputVariables') inputVariables: QueryList<ElementRef>;
  createMode = false;
  savedGradingGroups = [];
  savedGradingGroupsBackup = [];
  selectedGradingGroup: any;
  selectedGradingGroups = [];
  savedGradingGroupsLimit = 10;
  searchParams: ViewEditGradingGroupSearch = {
    name: '',
    description: '',
    type: ViewEditGradingGroupSearchType.all,
    onlyEditables: true
  };

  constructor(
    private service: GradingSystemService,
    private toastr: ToastrService,
    private changeDetectorRef: ChangeDetectorRef,
  ) { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  public ngOnInit(): void {
    this.fetchSimpleGradingSystems();
  }

  public resetSearchParams(): void {
    this.searchParams = {
      name: '',
      description: '',
      type: ViewEditGradingGroupSearchType.all,
      onlyEditables: true
    };
  }

  public onResetSelectedGradingSystem(): void {
    const backupEntryIndex = this.savedGradingGroups.findIndex(f => f.id === this.selectedGradingGroup.id);
    if (backupEntryIndex !== -1) {
      console.log(this.selectedGradingGroup, this.savedGradingGroups[backupEntryIndex], this.savedGradingGroupsBackup[backupEntryIndex]);
      this.savedGradingGroups[backupEntryIndex] = cloneDeep(this.savedGradingGroupsBackup[backupEntryIndex]);
      this.selectedGradingGroup = this.savedGradingGroups[backupEntryIndex];
    } else {
      // Should not happen.
      console.error('COULD NOT FIND BACKUIIP INDEX. SHOULD NOT HAPPEN!!!!');
    }
  }

  public onDeleteSelectedGradingSystem(): void {
    this.service.deleteGradingSystem(this.selectedGradingGroup.id).subscribe({
      next: (data) => {
        const indexToRemove = this.savedGradingGroups.findIndex(g => g.id === this.selectedGradingGroup.id);
        this.savedGradingGroups.splice(indexToRemove, 1);
        this.savedGradingGroupsBackup.splice(indexToRemove, 1);
        this.toastr.success(`${this.selectedGradingGroup.name} erfolgreich gelöscht!`);
        this.selectedGradingGroup = undefined;
      },
      error: err => {
        console.log(err);
        const errorObj = err.error;
        if (err.status === 0) {
          this.toastr.error('Could not connect to remote server!', 'Connection error');
        } else if (err.status === 401) {
          this.toastr.error('Either you are not authenticated or your session has expired', 'Authentication error');
        } else if (err.status === 403) {
          this.toastr.error('You don\'t have enought permissions', 'Authorization error');
        } else if (!errorObj.message && !errorObj.errors) {
          this.toastr.error(err.message ?? '', 'Unexpected error occured.');
        } else {
          this.toastr.error(errorObj.message, 'Error');
        }
      }
    });
  }

  public getFoundGradingSystems(): any[] {
    const nameRegex = new RegExp(this.searchParams.name, 'i');
    const selectedId = this.savedGradingGroups.find(g => this.selectedGradingGroup?.id === g.id)?.id;

    return this.savedGradingGroups.filter(g =>
      ((!this.searchParams.onlyEditables || g.editable)
        && g.name.match(nameRegex)
        && (g.public === (this.searchParams.type === 1) || this.searchParams.type === 0))
        || g.id === selectedId).sort((a, b) => a.name.localeCompare(b.name));
  }

  public getStyleForSelected(index: number): string {
    return index === this.selectedGradingGroup?.id ? 'border: 2px solid rgb(59, 53, 97); background-color: #ee76217f;' : '';
  }

  public getClassForSelected(index: number): string {
    return index !== this.selectedGradingGroup?.id ? 'options-menu-item options-menu-item-hover' : 'options-menu-item';
  }

  public onSaveGradingSystem() {
    const errors = this.checkGradingGroupValid();
    if (errors.length !== 0) {
      this.toastr.error(errors.join('\n'), 'Unvollständige Angaben');
      return;
    }

    const gradingSystem = this.marshalGradingSystem(this.selectedGradingGroup);
    if (!gradingSystem) {
      return;
    }
    let observable;

    if (this.createMode) {
      gradingSystem.isTemplate = true;
      observable = this.service.createGradingSystem(gradingSystem);
    } else {
      observable = this.service.updateGradingSystem(gradingSystem);
    }
    observable.subscribe({
        next: value => {
          this.toastr.success(`${value.name} erfolgreich gespeichert!`);
          if (this.createMode) {
            this.selectedGradingGroup.id = value.id;
            this.savedGradingGroups.push(this.selectedGradingGroup);
            this.savedGradingGroupsBackup.push(cloneDeep(this.selectedGradingGroup));
            this.createMode = false;
          } else {
            const backupEntryIndex = this.savedGradingGroupsBackup.findIndex(g => g.id === this.selectedGradingGroup.id);
            if (backupEntryIndex) {
              this.savedGradingGroupsBackup[backupEntryIndex] = cloneDeep(this.selectedGradingGroup);
            } else {
              console.log('BLYAT');
            }
          }
        },
        error: err => {
          console.log(err);
          const errorObj = err.error;
          if (err.status === 0) {
            this.toastr.error('Could not connect to remote server!', 'Connection error');
          } else if (err.status === 401) {
            this.toastr.error('Either you are not authenticated or your session has expired', 'Authentication error');
          } else if (err.status === 403) {
            this.toastr.error('You don\'t have enought permissions', 'Authorization error');
          } else if (!errorObj.message && !errorObj.errors) {
            this.toastr.error(err.message ?? '', 'Unexpected error occured.');
          } else {
            console.log(err.error.errors);
            this.toastr.error(
              `<ul>${err.error.errors.map(e => '<li>' + e + '</li>').join('\n')}</ul>`,
              err.error.message,
              { enableHtml: true });
          }
        }
      }
    );
  }

  public onGradingGroupSelection(id: number): void {
    this.createMode = false;
    const foundGradingGroupIndex = this.savedGradingGroups.findIndex(group => group.id === id);
    let foundGradingGroup = this.savedGradingGroups[foundGradingGroupIndex];
    if (!foundGradingGroup.formula) {
      this.service.getDraftGradingSystemById(id).subscribe({
        next: (data) => {
          if(data) {
            if (foundGradingGroup.editable) {
              Object.assign(foundGradingGroup, data);
            } else {
              const clonedData = cloneDeep(data);
              const clonedFoundGradingGroup = cloneDeep(foundGradingGroup);
              foundGradingGroup = Object.assign(clonedFoundGradingGroup, clonedData);
            }
            const gradingSystem = JSON.parse(data.formula);
            foundGradingGroup.stations = gradingSystem.stations.map(station => ({
              name: station.displayName,
              id: station.id,
              variables: station.variables.map(variable => ({
                name: variable.displayName,
                type: 'variable',
                typeHint: 'variableRef',
                value: variable.id
              })),
              formula: {
                valid: true,
                data: station.formula
              },
              idCount: Math.max(...station.variables.map(v => v.id))
            }));
            foundGradingGroup.formula = ({
              valid: true,
              data: gradingSystem.formula
            });
            foundGradingGroup.stationVariables = gradingSystem.stations.map(station => ({
              name: station.displayName,
              value: station.id,
              type: 'variable',
              typeHint: 'variableRef'
            }));
            foundGradingGroup.idCount = Math.max(...foundGradingGroup.stations.map(s => s.id));
            if (foundGradingGroup.editable) {
              this.savedGradingGroupsBackup[foundGradingGroupIndex] = cloneDeep(foundGradingGroup);
            }
            this.selectedGradingGroup = foundGradingGroup;

            console.log(this.selectedGradingGroup);
          }
        }
      });
    } else {
      this.selectedGradingGroup = foundGradingGroup;
    }
    console.log('MEMORY', this.savedGradingGroups, this.savedGradingGroupsBackup);
  }

  public onCreateGradingGroupClick(): void {
    this.createMode = true;
    this.selectedGradingGroup = {
      name: ``,
      stations: [],
      stationVariables: [],
      formula: { valid: false, data: {} },
      idCount: 0,
      editable: true,
      public: false,
    };
  }

  addStation() {
    const group = this.selectedGradingGroup;
    group.stations.push({name: `Station ${++group.idCount}`,
      id: group.idCount, variables: [],
      idCount: 0, formula: {valid: false, data: {} }});

      group.stationVariables.push({
      name: `Station ${group.idCount}`,
      value: group.idCount,
      type: 'variable',
      typeHint: 'variableRef',
      spaces: 0,
      priority: 0
    });

    group.stationVariables = cloneDeep(group.stationVariables);
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

  upadteStationName(stationId, newName) {
    if(this.selectedGradingGroup.stations.map(s => s.name.trim()).includes(newName.trim())) {
      this.toastr.warning('Stationsnamen müssen innerhalb einer Gruppen einzigartig sein!', 'Achtung:');
    }

    this.selectedGradingGroup.stations[stationId].name = newName;
    this.selectedGradingGroup.stationVariables[stationId].name = newName;

    this.selectedGradingGroup.stationVariables = cloneDeep(this.selectedGradingGroup.stationVariables);
  }

  // change variable
  change(station, newName) {
    station.variables = cloneDeep(station.variables.filter(x => x.name !== ''));

    if(station.variables.filter(v => v.name.trim() === newName.trim()).length > 1) {
      this.toastr.warning('Variablennamen müssen innerhalb einer Station einzigartig sein!', 'Achtung:');
    }
  }

  duplicateStation(station, stationId) {
    if(!this.selectedGradingGroup.stations[stationId].formula.valid) {
      this.toastr.error(
        `${this.selectedGradingGroup.stations[stationId].name} kann nicht dupliziert werden da die Formel ungültig ist`
        ,'Fehler beim Duplizieren');
      return;
    }
    this.selectedGradingGroup.stations.splice(stationId+1, 0, cloneDeep(
      Object.assign({}, station, {name: station.name + ' Kopie', id: ++this.selectedGradingGroup.idCount})));

    this.selectedGradingGroup.stationVariables.splice(stationId + 1, 0, {
      name: this.selectedGradingGroup.stations[stationId+1].name,
      value: this.selectedGradingGroup.idCount,
      type: 'variable',
      typeHint: 'variableRef',
      spaces: 0,
      priority: 0
    });
    this.selectedGradingGroup.stationVariables = cloneDeep(this.selectedGradingGroup.stationVariables);
    this.toastr.info(`${this.selectedGradingGroup.stations[stationId+1].name} erfolgreich erstellt.`);
  }

  deleteStation(stationId) {
    this.selectedGradingGroup.stations.splice(stationId, 1);

    this.selectedGradingGroup.stationVariables.splice(stationId, 1);
    this.selectedGradingGroup.stationVariables = cloneDeep(this.selectedGradingGroup.stationVariables);
  }

  private checkGradingGroupValid(): any[] {
    const invalidErrors = [];

    const group: any = this.selectedGradingGroup;

    if (group.stations.length > 0) {
      for (const station of group.stations) {
        if (!station.formula.valid) {
          invalidErrors.push(`${group.name} -> ${station.name}`);
        }
        if (group.stations.filter(s => s.name.trim() === station.name.trim()).length > 1) {
          invalidErrors.push(`${group.name} -> ${station.name} ist kein einzigartiger Name`);
        }

        if (station.variables.length > 0) {
          for (const variable of station.variables) {
            if (station.variables.filter(v => v.name.trim() === variable.name.trim()).length > 1) {
              invalidErrors.push(`${group.name} -> ${station.name} -> ${variable.name} ist kein einzigartiger Name`);
            }
          }
        }
      }
    }

    return invalidErrors;
  }

  private fetchSimpleGradingSystems(): void {
    this.service.getSimpleDraftGradingSystems().subscribe(
      {
        next: (data) => {
          this.savedGradingGroups = data;
          this.savedGradingGroupsBackup = cloneDeep(data);
        },
        error: (err) => {
          if (err.status === 0) {
            this.toastr.error('Could not connect', 'Connection Error');
          } else {
            this.toastr.error(err);
          }
        }
      }
    );
  }

  private marshalGradingSystem(gradingSystem: any): ViewEditGradingGroup {
    const gradingGroup: any = cloneDeep(gradingSystem);
    if (!gradingGroup) {
      this.toastr.error('Eine Bewertungssystem muss ausgewählt werden!');
      return;
    }

    const marshaledGradingSystem: ViewEditGradingGroup = {
      id: gradingGroup.id,
      name: gradingGroup.name,
      description: gradingGroup.description,
      isPublic: gradingGroup.public,
      isTemplate: true,
      formula: JSON.stringify({
        stations: gradingGroup.stations.map((station, stationId) => ({
          displayName: station.name,
          id: station.id,
          variables: station.variables.map(variable => ({
            displayName: variable.name,
            id: variable.value
          })),
          formula: station.formula.data
        })),
        formula: gradingGroup.formula.data
      }),
    };

    return marshaledGradingSystem;
  }
}
