import { Component, OnInit } from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {UserPasswordReset} from '../../dtos/userPasswordReset';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  resetPasswordForm: UntypedFormGroup;
  submitted = false;
  // Error flag
  error = false;
  success = false;
  errorMessage = '';
  token;

  constructor(private formBuilder: UntypedFormBuilder,
              private authService: AuthService,
              private router: Router,
              private route: ActivatedRoute) {
    this.resetPasswordForm = this.formBuilder.group({
      passwordOne: ['', [Validators.required]],
      passwordTwo: ['', [Validators.required]]
    });
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
  }

  ngOnInit(): void {
  }

  resetPassword() {
    this.submitted = true;
    if (this.resetPasswordForm.valid) {
      if (this.resetPasswordForm.controls.passwordOne.value === this.resetPasswordForm.controls.passwordTwo.value) {
        const userPasswordReset: UserPasswordReset = new UserPasswordReset(this.token, this.resetPasswordForm.controls.passwordOne.value);
        alert(userPasswordReset.token);
        alert(userPasswordReset.password);
        this.authService.resetPassword(userPasswordReset).subscribe({
          next: () => {
            console.log('Password successfully reset');
            alert('Password successfully reset');
            this.router.navigate(['/login']);
            //TODO add alert from designer
          },
          error: error => {
            console.log('Could not reset password due to:');
            console.log(error);
            this.error = true;
            if (typeof error.error === 'object') {
              this.errorMessage = error.error.error;
            } else {
              this.errorMessage = error.error;
            }
          }
        });
      }
    }
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

}
