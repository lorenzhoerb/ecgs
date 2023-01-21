import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service';


@Component({
  selector: 'app-create-competition-select-grading-system-dialog',
  templateUrl: './create-competition-select-grading-system-dialog.component.html',
  styleUrls: ['./create-competition-select-grading-system-dialog.component.scss']
})
export class CreateCompetitionSelectGradingSystemDialogComponent implements OnInit {

  simpleGradingSystems: any[];
  searchParams = {
    name: ''
  };

  constructor(
    @Inject(MAT_DIALOG_DATA) data,
    private dialogRef: MatDialogRef<CreateCompetitionSelectGradingSystemDialogComponent>,
    ) {
    this.simpleGradingSystems = data;
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {

  }

  onGradingSystemSelection(id: number): void {
    this.dialogRef.close({
      selectedGradingSystemId: id
    });
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  getMatchingSimpleGradingSystems(): any[] {
    const nameRegex = new RegExp(this.searchParams.name, 'i');

    return this.simpleGradingSystems
      .filter(g => g.name.match(nameRegex))
      .sort((a, b) => a.name.localeCompare(b.name));
  }
}
