import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {RegisterRequest} from '../../dtos/auth-request';
import {ToastrService} from 'ngx-toastr';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';
import {UserService} from 'src/app/services/user.service';
import {ClubManagerTeamImportDto} from 'src/app/dtos/club-manager-team';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  selectedRole;
  teamNameValid = false;
  registerForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  hide = true;

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService,
              private router: Router, private notification: ToastrService,
              private userService: UserService) {
    this.registerForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      gender: ['', [Validators.required]],
      dateOfBirth: ['', [Validators.required]],
      type: ['', [Validators.required]],
      teamName: ['', []]
    });
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit() {
  }


  afterSuccessfulRegistration() {
    this.notification.success('Successfully registered!');
    this.router.navigate(['/login']);
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  registerUser() {
    this.submitted = true;
    if (this.registerForm.valid && this.isTeamNameValid()) {
      const registerRequest: RegisterRequest = new RegisterRequest(this.registerForm.controls.email.value,
        this.registerForm.controls.password.value,
        this.registerForm.controls.firstName.value,
        this.registerForm.controls.lastName.value,
        this.registerForm.controls.gender.value,
        this.registerForm.controls.dateOfBirth.value,
        this.registerForm.controls.type.value,
        this.registerForm.controls.teamName.value,
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
        // const newTeam: ClubManagerTeamImportDto = {
        //   teamName: this.registerForm.controls.teamName.value,
        //   teamMembers: [{
        //     firstName: 'placeholder',
        //     lastName: 'placeholder',
        //     gender: 'MALE',
        //     dateOfBirth: '2022-03-03',
        //     email: this.registerForm.controls.email.value
        //   }]
        // };
        this.notification.success('Successfully registered!');
        this.router.navigate(['/login']);
      },
      error: error => {
        console.log('Could not register due to:');
        console.log(error);
        if (typeof error.error === 'object') {
          console.log('error.error.error object: ' + error.error.error);
          this.notification.error(error.error.error);
        } else {
          console.log('error.error object: ', error.error);
          const parsedError = JSON.parse(error.error);
          console.log(parsedError);
          this.notification.error(
            `<ul>${parsedError.errors.map(e => '<li>' + e + '</li>').join('\n')}</ul>`,
            parsedError.message,
            {enableHtml: true});
        }
      }
    });
  }

  private isTeamNameValid(): boolean {
    return this.selectedRole === 'PARTICIPANT'
      || (this.registerForm.controls.teamName.value.length > 0
        && this.registerForm.controls.teamName.value.length < 256);
  }
}
