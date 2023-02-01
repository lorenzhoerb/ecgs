import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {UserPasswordReset} from '../../dtos/userPasswordReset';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, {LocalizeService} from '../../services/localization/localization.service';

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
  hide = true;

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

      if (this.authService.isLoggedIn()) {
        this.router.navigate(['/']);
        this.notification.error(this.localize.resetPasswordErrorLoggedIn);
        return;
      }

      if (this.token === null || this.token === undefined || this.token.length !== 32) {
        this.notification.error(this.localize.resetPasswordErrorMalformedToken);
        this.router.navigate(['/login']);
      }
    });
  }

  public get localize(): LocalizeService {
    return LocalizationService;
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
            this.notification.success(this.localize.resetPasswordSuccess);
            this.router.navigate(['/login']);
          },
          error: error => {
            console.log('Could not reset password due to:');
            if (typeof error.error === 'object') {
              if (error.error.error === 'Not Found') {
                console.log('No user with given token found!');
                this.notification.error(this.localize.resetPasswordError);
              }
            } else {
              this.notification.error(error.error);
            }
          }
        });
      } else {
        this.notification.error(this.localize.passwordsMustMatch);
      }
    }
  }

}
