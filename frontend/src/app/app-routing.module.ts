import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import { UidemoComponent } from './components/uidemo/uidemo.component';
import { environment } from 'src/environments/environment';
import {RegisterComponent} from './components/register/register.component';
import {CreateCompetitionComponent} from './components/competition/create-competition/create-competition.component';


const routbuilding: Routes = [
  {path: '', component: HomeComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {
    path: 'competition', children:
      [{path: 'create', canActivate: [AuthGuard], component: CreateCompetitionComponent}]
  },
  {path: '**', redirectTo: ''},
];

// These Routes will be visible in Dev mode, but not when
// build with `--configuration production` flags
if(!environment.production) {
  routbuilding.push({path: 'ui-demo', component: UidemoComponent});
}

const routes: Routes = routbuilding;

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
