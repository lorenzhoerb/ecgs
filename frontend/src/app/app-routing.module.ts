import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {CompetitionComponent} from './components/competition/competition.component';
import { UidemoComponent } from './components/uidemo/uidemo.component';
import { environment } from 'src/environments/environment';
import {RegisterComponent} from './components/register/register.component';
import { ClubManagerImportTeamComponent } from './components/club-manager-import-team/club-manager-import-team.component';
import { CompetitionCalenderViewComponent } from './components/competition-calender-view/competition-calender-view.component';
import {ForgotPasswordComponent} from './components/forgot-password/forgot-password.component';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {ChangePasswordComponent} from './components/change-password/change-password.component';
import {CreateCompetitionComponent} from './components/competition/create-competition/create-competition.component';
import {ViewParticipantsComponent} from './components/competition/view-participants/view-participants.component';


const routbuilding: Routes = [
  {path: '', component: HomeComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'forgot', component: ForgotPasswordComponent},
  {path: 'reset', component: ResetPasswordComponent},
  {path: 'changePassword', canActivate: [AuthGuard], component: ChangePasswordComponent},
  {path: 'ui-demo', component: UidemoComponent},
  {path: 'user', children: [
      {path: 'calendar', component: CompetitionCalenderViewComponent},
      {path: 'import-team', component: ClubManagerImportTeamComponent}
    ]
  },
  {
    path: 'competition', children:
      [
        {path: 'create', canActivate: [AuthGuard], component: CreateCompetitionComponent},
        {path: ':id', component: CompetitionComponent},
        //{path: ':id/participants', canActivate: [AuthGuard], component: ViewParticipantsComponent},
      ]
  },
  {path: '**', component: UidemoComponent},
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
