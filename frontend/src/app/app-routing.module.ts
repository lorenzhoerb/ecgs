import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {TournamentManagerGuard} from './guards/tournamentManager.guard';
import {ClubManagerGuard} from './guards/clubManager.guard';
import {CompetitionComponent} from './components/competition/competition.component';
import {UidemoComponent} from './components/uidemo/uidemo.component';
import {environment} from 'src/environments/environment';
import {RegisterComponent} from './components/register/register.component';
import {ClubManagerImportTeamComponent} from './components/club-manager-import-team/club-manager-import-team.component';
import {
  CompetitionCalenderViewComponent
} from './components/competition-calender-view/competition-calender-view.component';
import {ForgotPasswordComponent} from './components/forgot-password/forgot-password.component';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {ChangePasswordComponent} from './components/change-password/change-password.component';
import {CreateCompetitionComponent} from './components/competition/create-competition/create-competition.component';
import {CompetitionListViewComponent} from './components/competition-list-view/competition-list-view.component';
import { ImportFlagsComponent } from './components/import-flags/import-flags.component';


const routbuilding: Routes = [
  {path: '', component: CompetitionListViewComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'forgot', component: ForgotPasswordComponent},
  {path: 'reset', component: ResetPasswordComponent},
  {path: 'changePassword', canActivate: [AuthGuard], component: ChangePasswordComponent},
  {path: 'competition', children:[
      {path: 'list', component: CompetitionListViewComponent},
      {path: 'create', canActivate: [TournamentManagerGuard], component: CreateCompetitionComponent},
      {path: ':id', component: CompetitionComponent},
      {path: 'edit/:id', canActivate: [TournamentManagerGuard], component: CreateCompetitionComponent}
    ]
  },
  {path: 'user', children: [
      {path: 'calendar', component: CompetitionCalenderViewComponent},
      {path: 'import-team', canActivate: [ClubManagerGuard], component: ClubManagerImportTeamComponent},
      {path: 'flags', canActivate: [ClubManagerGuard], component: ImportFlagsComponent}
    ]
  },
  {path: '**', component: CompetitionListViewComponent},
];

// These Routes will be visible in Dev mode, but not when
// build with `--configuration production` flags
if(!environment.production) {
  routbuilding.push({path: 'ui-demo', component: UidemoComponent});
}

// routbuilding.push({path: '**', redirectTo: ''});

const routes: Routes = routbuilding;

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
