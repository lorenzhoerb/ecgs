import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  forgotPasswordForm: UntypedFormGroup;
  submitted = false;
  success = false;

  constructor(private formBuilder: UntypedFormBuilder,
              private authService: AuthService,
              private router: Router,
              private notification: ToastrService) {
    this.forgotPasswordForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  requestPasswordReset() {
    this.submitted = true;
    if (this.forgotPasswordForm.valid) {
      this.authService.requestPasswordReset(this.forgotPasswordForm.controls.email.value).subscribe({
        next: () => {
          this.notification.success(this.localize.passwordResetLinkSent);
          this.router.navigate(['/login']);
        },
        error: error => {
          console.log('error.error object: ' , error.error);
          if (error.error.errors) {
            this.notification.error(
              `<ul>${error.error.errors.map(e => '<li>' + e + '</li>').join('\n')}</ul>`,
              'Validierungsfehler',
              {enableHtml: true});
          } else {
            if (error.error.error === 'Not Found') {
              this.notification.error('Unter der angegebenen E-Mail Adresse wurde kein Eintrag im System gefunden.');
            }
          }
        }
      });
    } else {
      this.notification.error(this.localize.enterValidMailForReset);
    }
  }

  ngOnInit(): void {
  }

}
