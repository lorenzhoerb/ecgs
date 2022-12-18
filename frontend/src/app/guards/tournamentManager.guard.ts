import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import {AuthService} from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class TournamentManagerGuard implements CanActivate {

  constructor(private authService: AuthService,
              private toastr: ToastrService,
              private router: Router) {}

  canActivate(): boolean {
    console.log(this.authService.isLoggedIn());
    if (this.authService.isLoggedIn() && this.authService.getUserRole() === 'TOURNAMENT_MANAGER') {
      return true;
    } else {
      this.toastr.warning('Leider haben Sie keine Berechtigung auf diese Seite zuzugreifen. Sie wurden daher umgeleitet!');
      this.router.navigate(['/']);
      return false;
    }
  }
}
