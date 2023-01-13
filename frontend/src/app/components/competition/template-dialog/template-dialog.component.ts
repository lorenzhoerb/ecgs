import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { TemplateState } from 'src/app/datatypes/templateAction';
import { GradingSystemService } from 'src/app/services/grading-system.service';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service';


@Component({
  selector: 'app-template-dialog',
  templateUrl: './template-dialog.component.html',
  styleUrls: ['./template-dialog.component.scss']
})
export class TemplateDialogComponent implements OnInit {

  templateForm: UntypedFormGroup;
  title: string;
  gradingSystem: any;
  submitted = false;

  constructor(
    private fb: UntypedFormBuilder,
    private toastr: ToastrService,
    private gradingSystemService: GradingSystemService,
    private dialogRef: MatDialogRef<TemplateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) data) {
    this.gradingSystem = data.gradingSystem;
    this.title = data.gradingSystem.name;
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.templateForm = this.fb.group({
      title: [this.gradingSystem.name, [Validators.required]],
      description: ['', [Validators.required]],
      isPublic: [false, []]
    });
  }

  save() {
    this.submitted = true;
    if (!this.templateForm.valid) {
      this.toastr.error('Bitte füllen Sie alle benötigten Felder aus', 'Unvollständige Angaben');
      return;
    }

    const gradingSystem = Object.assign({}, this.gradingSystem, {
      name: this.templateForm.value.title,
      description: this.templateForm.value.description,
      isPublic: this.templateForm.value.isPublic
    });

    this.gradingSystemService.createGradingSystem(gradingSystem)
      .subscribe({
        next: value => {
          this.toastr.success(`${this.templateForm.value.title} erfolgreich als Template gespeichert!`);
          this.dialogRef.close({
            save: true,
            value: TemplateState.saved
          });
        },
        error: err => {
          console.log(err.error.errors);
          this.toastr.error(
            `<ul>${err.error.errors.map(e => '<li>' + e + '</li>').join('\n')}</ul>`,
            err.error.message,
            { enableHtml: true });
        }
      });
  }

  close() {
    this.dialogRef.close({ save: false, value: null });

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
