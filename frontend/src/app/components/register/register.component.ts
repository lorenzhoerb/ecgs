import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {RegisterRequest} from '../../dtos/auth-request';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  hide = true;

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService,
              private router: Router, private notification: ToastrService) {
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

  public get localize(): LocalizeService {
    return LocalizationService;
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
    this.authService.registerUser(registerRequest).subscribe({
      next: () => {
        this.notification.success('Successfully registered!');
        this.router.navigate(['/login']);
      },
      error: error => {
        console.log('Could not register due to:');
        console.log(error);
        if (typeof error.error === 'object') {
          console.log('error.error.error object: ' +error.error.error);
          this.notification.error(error.error.error);
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
  }

  ngOnInit() {
  }

}
