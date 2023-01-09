import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {UserCredentialUpdate} from '../../dtos/userCredentialUpdate';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  changePasswordForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  hide = true;

  constructor(private formBuilder: UntypedFormBuilder,
              private authService: AuthService,
              private router: Router,
              private notification: ToastrService) {
    this.changePasswordForm = this.formBuilder.group({
      email: [authService.getUserName(), [Validators.required, Validators.email]],
      passwordOne: ['', [Validators.required, Validators.minLength(8)]],
      passwordTwo: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  changePassword() {
    this.submitted = true;
    if (this.changePasswordForm.valid) {
      if (this.changePasswordForm.controls.passwordOne.value === this.changePasswordForm.controls.passwordTwo.value) {
        const userCredentialUpdate: UserCredentialUpdate = new UserCredentialUpdate(
          this.changePasswordForm.controls.email.value,
          this.changePasswordForm.controls.passwordOne.value);
        this.authService.changePassword(userCredentialUpdate).subscribe({
          next: () => {
            this.notification.success('Password successfully changed');
            this.router.navigate(['']);
          },
          error: error => {
            console.log('Could not change password due to:');
            if (typeof error.error === 'object') {
              error.error.errors.map(err => {
                this.notification.error(err);
                console.log(err);
              });
            } else {
              console.log('error.error object: ' , error.error);
              const parsedError = JSON.parse(error.error);
              console.log(parsedError);
              parsedError.errors.map(err => {
                this.notification.error(err);
                console.log(err);
              });
            }
          }
        });
      } else {
        this.notification.error('Both passwords must match!');
      }
    }
  }

  ngOnInit(): void {
  }

}
