import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {AuthRequest, RegisterRequest} from '../../dtos/auth-request';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      gender: ['', [Validators.required]],
      dateOfBirth: ['', [Validators.required]],
      type: ['', [Validators.required]],
    });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  registerUser() {
    this.submitted = true;
    if (this.registerForm.valid) {
      const registerRequest: RegisterRequest = new RegisterRequest(this.registerForm.controls.email.value,
        this.registerForm.controls.password.value,
        this.registerForm.controls.firstName.value,
        this.registerForm.controls.lastName.value,
        this.registerForm.controls.gender.value,
        this.registerForm.controls.dateOfBirth.value,
        this.registerForm.controls.type.value
      );
      this.sendUserRegistration(registerRequest);
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Send registration through the auth-service
   *
   * @param registerRequest register data from the user register form
   */
  sendUserRegistration(registerRequest: RegisterRequest) {
    console.log('RegisterRequest gets send', registerRequest);
    this.authService.registerUser(registerRequest).subscribe({
      next: () => {
        console.log('Successfully registered  ');
        alert('Gratulation du hast nen user erstellt!');
        //TODO add alert from designer
      },
      error: error => {
        console.log('Could not register due to:');
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

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  ngOnInit() {
  }

}
