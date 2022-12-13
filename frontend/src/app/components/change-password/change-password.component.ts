import {Component, OnInit, SecurityContext} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {UserCredentialUpdate} from '../../dtos/userCredentialUpdate';
import {Router} from '@angular/router';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  changePasswordForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';

  constructor(private formBuilder: UntypedFormBuilder,
              private authService: AuthService,
              private router: Router) {
    this.changePasswordForm = this.formBuilder.group({
      email: [authService.getUserName(), [Validators.required, Validators.email]],
      passwordOne: ['', [Validators.required]],
      passwordTwo: ['', [Validators.required]]
    });
  }

  changePassword() {
    this.submitted = true;
    if (this.changePasswordForm.valid) {
      if (this.changePasswordForm.controls.passwordOne.value === this.changePasswordForm.controls.passwordTwo.value) {
        const userCredentialUpdate: UserCredentialUpdate = new UserCredentialUpdate(
          this.changePasswordForm.controls.email.value,
          this.changePasswordForm.controls.passwordOne.value);
        alert(userCredentialUpdate.email);
        alert(userCredentialUpdate.password);
        this.authService.changePassword(userCredentialUpdate).subscribe({
          next: () => {
            console.log('Password successfully changed');
            alert('Password successfully changed');
            this.router.navigate(['']);
            //TODO add alert from designer
          },
          error: error => {
            console.log('Could not change password due to:');
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

  ngOnInit(): void {
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }
}
