import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {ToastrService} from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class ClubManagerGuard implements CanActivate {

  constructor(private authService: AuthService,
              private router: Router,
              private toastr: ToastrService) {}

  canActivate(): boolean {
    if (this.authService.isLoggedIn() &&
        (this.authService.getUserRole() === 'CLUB_MANAGER'
          || this.authService.getUserRole() === 'TOURNAMENT_MANAGER')) {

      return true;
    } else {
      this.toastr.warning('Leider haben Sie keine Berechtigung auf diese Seite zuzugreifen. Sie wurden daher umgeleitet!');
      this.router.navigate(['/']);
      return false;
    }
  }
}
