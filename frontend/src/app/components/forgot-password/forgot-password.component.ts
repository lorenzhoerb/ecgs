import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service';

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
          console.log('Password reset link has been sent');
          this.notification.success('Password reset link has been sent');
          this.router.navigate(['/login']);
        },
        error: error => {
          console.log('error.error object: ', error.error.error);
          this.notification.error('Error: Email ' + error.error.error);
        }
      });
    } else {
      this.notification.error('Invalid input, please enter valid Email');
    }
  }

  ngOnInit(): void {
  }

}
