import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { UserInfoDto } from 'src/app/dtos/userInfoDto';
import { AuthService } from 'src/app/services/auth.service';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service' ;
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  @Output() showMenu = new EventEmitter();
  public loggedIn = false;
  public user?: UserInfoDto;


  constructor(public authService: AuthService, public userService: UserService, private router: Router) { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.loggedIn = this.authService.isLoggedIn();
    if(this.loggedIn) {
      this.userService.getUserInfo().subscribe({
        next: data => {
          this.user = data;
          console.log(data);
        },
        error: error => {
          console.error('Error fetching competition information', error);
          this.loggedIn = false;
          // @TODO: route to home or throw exception
        }
      });
    }

    this.router.events.subscribe((event) => {
      const tempLoggedIn = this.authService.isLoggedIn();
      if(this.loggedIn === tempLoggedIn) {
        return;
      }
      this.loggedIn = tempLoggedIn;
      if(this.loggedIn) {
        this.userService.getUserInfo().subscribe({
          next: data => {
            this.user = data;
            console.log(data);
          },
          error: error => {
            console.error('Error fetching competition information', error);
            this.loggedIn = false;
            // @TODO: route to home or throw exception
          }
        });
      }
    });
  }
}
