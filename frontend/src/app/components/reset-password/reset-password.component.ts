import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {UserPasswordReset} from '../../dtos/userPasswordReset';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  resetPasswordForm: UntypedFormGroup;
  submitted = false;
  success = false;
  token;

  constructor(private formBuilder: UntypedFormBuilder,
              private authService: AuthService,
              private router: Router,
              private route: ActivatedRoute,
              private notification: ToastrService) {
    this.resetPasswordForm = this.formBuilder.group({
      passwordOne: ['', [Validators.required, Validators.minLength(8)]],
      passwordTwo: ['', [Validators.required, Validators.minLength(8)]],
    });
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      if (this.token.length !== 32) {
        this.notification.error('Redirected because of malformed token');
        this.router.navigate(['/login']);
      }
    });
  }

  ngOnInit(): void {
  }

  resetPassword() {
    this.submitted = true;
    if (this.resetPasswordForm.valid) {
      if (this.resetPasswordForm.controls.passwordOne.value === this.resetPasswordForm.controls.passwordTwo.value) {
        const userPasswordReset: UserPasswordReset = new UserPasswordReset(this.token, this.resetPasswordForm.controls.passwordOne.value);
        this.authService.resetPassword(userPasswordReset).subscribe({
          next: () => {
            this.notification.success('Password successfully reset');
            this.router.navigate(['/login']);
          },
          error: error => {
            console.log('Could not reset password due to:');
            if (typeof error.error === 'object') {
              console.log('error.error.errors: ' + error.error.errors);
              this.notification.error('' + error.error.errors);
            } else {
              this.notification.error(error.error);
            }
          }
        });
      } else {
        this.notification.error('Both passwords must match!');
      }
    }
  }

}
