import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {AuthService} from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ClubManagerGuard implements CanActivate {

  constructor(private authService: AuthService,
              private router: Router) {}

  canActivate(): boolean {
    console.log(this.authService.isLoggedIn());
    if (this.authService.isLoggedIn() &&
        (this.authService.getUserRole() === 'CLUB_MANAGER'
          || this.authService.getUserRole() === 'TOURNAMENT_MANAGER')) {

      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}
