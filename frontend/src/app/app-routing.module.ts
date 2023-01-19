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
import {CreateCompetitionComponent} from './components/competition/create-competition/create-competition.component';
import {CompetitionGradingComponent} from './components/competition/competition-grading/competition-grading.component';
import { ViewAndEditGradingSystemComponent } from './components/view-and-edit-grading-system/view-and-edit-grading-system.component';
import { CompetitionListViewComponent } from './components/competition-list-view/competition-list-view.component';
import { ImportFlagsComponent } from './components/import-flags/import-flags.component';
import {ManageParticipantsComponent} from './components/competition/manage-participants/manage-participants.component';
import {ChangeUserPassPictureComponent} from './components/change-user-pass-picture/change-user-pass-picture.component';
import {ClubManagerEditComponent} from './components/club-manager-edit/club-manager-edit.component';

const routbuilding: Routes = [
  {path: '', component: CompetitionListViewComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'forgot', component: ForgotPasswordComponent},
  {path: 'reset', component: ResetPasswordComponent},
  {path: 'competition', children:[
      {path: 'list', component: CompetitionListViewComponent},
      {path: 'create', canActivate: [TournamentManagerGuard], component: CreateCompetitionComponent},
      {path: ':id', children: [
          {path: '', component: CompetitionComponent},
          {path: 'participants', canActivate: [TournamentManagerGuard], component: ManageParticipantsComponent},
          {path: 'grading', component: CompetitionGradingComponent}
        ]},
      {path: 'edit/:id', canActivate: [TournamentManagerGuard], component: CreateCompetitionComponent},
    ]
  },
  {path: 'grading-systems', canActivate: [TournamentManagerGuard], component: ViewAndEditGradingSystemComponent},
  {path: 'user', children: [
      {path: 'calendar', component: CompetitionCalenderViewComponent},
      {path: 'import-team', canActivate: [ClubManagerGuard], component: ClubManagerImportTeamComponent},
      {path: 'flags', canActivate: [ClubManagerGuard], component: ImportFlagsComponent},
      {path: 'change-picture-password', canActivate: [AuthGuard], component: ChangeUserPassPictureComponent},
      {path: 'edit-team', canActivate: [ClubManagerGuard] , component: ClubManagerEditComponent},
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
