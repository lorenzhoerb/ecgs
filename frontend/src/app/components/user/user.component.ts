import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Router} from '@angular/router';
import {UserInfoDto} from 'src/app/dtos/userInfoDto';
import {AuthService} from 'src/app/services/auth.service';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';
import {UserService} from 'src/app/services/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  @Output() showMenu = new EventEmitter();
  public loggedIn = false;
  public user?: UserInfoDto;
  public language: string;
  public picturePath: string;

  constructor(public authService: AuthService, public userService: UserService, private router: Router) {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.language = this.localize.getLanguage();
    this.loggedIn = this.authService.isLoggedIn();
    this.picturePath = '../../../assets/user_image.jpg';
    if (this.loggedIn) {
      this.userService.updateUserInfo();
      this.userService.userInfoDto$.subscribe({
        next: (dto) => {
          this.user = dto;
          this.picturePath = this.user.picturePath;

        },
        error: () => console.warn('Error during profile update')
      });
    }

    this.router.events.subscribe((event) => {
      const tempLoggedIn = this.authService.isLoggedIn();
      if (this.loggedIn === tempLoggedIn) {
        return;
      }
      this.loggedIn = tempLoggedIn;
      if (this.loggedIn) {
        this.userService.updateUserInfo();
        this.userService.userInfoDto$.subscribe({
          next: (dto) => {
            this.user = dto;
            this.picturePath = this.user.picturePath;
          },
          error: () => console.warn('Error during profile update')
        });
      }
    });
  }
}
