import {BrowserModule} from '@angular/platform-browser';
import { DragDropModule } from '@angular/cdk/drag-drop';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {CompetitionComponent} from './components/competition/competition.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { RegisterComponent } from './components/register/register.component';
import { UidemoComponent } from './components/uidemo/uidemo.component';
import { ContentCardComponent } from './components/content-card/content-card.component';
import { CreateCompetitionComponent } from './components/competition/create-competition/create-competition.component';
import { UserComponent } from './components/user/user.component';
import { GoldenRatioContainerComponent } from './components/containers/golden-ratio-container/golden-ratio-container.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonToggleModule} from '@angular/material/button-toggle';
import { ViewParticipantsComponent } from './components/competition/view-participants/view-participants.component';
import {ErrorComponent} from './components/error/error.component';
import {ToastrModule} from 'ngx-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { FormularEditorComponent } from './components/competition/formular-editor/formular-editor.component';
import { HeaderModalComponent } from './components/header-modal/header-modal.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import { CompetitionListViewComponent } from './components/competition-list-view/competition-list-view.component';
import { CompetitionListViewEntryComponent }
  from './components/competition-list-view/competition-list-view-entry/competition-list-view-entry.component';
import { CompetitionListViewFilterComponent }
  from './components/competition-list-view/competition-list-view-filter/competition-list-view-filter.component';
import { ClubManagerImportTeamComponent } from './components/club-manager-import-team/club-manager-import-team.component';
import { CompetitionCalenderViewComponent } from './components/competition-calender-view/competition-calender-view.component';
import { CompetitionHeaderComponent } from './components/competition-header/competition-header.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    CompetitionComponent,
    RegisterComponent,
    UidemoComponent,
    ContentCardComponent,
    CreateCompetitionComponent,
    UserComponent,
    GoldenRatioContainerComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    ChangePasswordComponent,
    CompetitionListViewComponent,
    CompetitionListViewEntryComponent,
    CompetitionListViewFilterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    ChangePasswordComponent,
    ViewParticipantsComponent,
    ErrorComponent,
    FormularEditorComponent,
    HeaderModalComponent,
    ClubManagerImportTeamComponent,
    CompetitionCalenderViewComponent,
    CompetitionHeaderComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    MatIconModule,
    MatButtonToggleModule,
    ToastrModule.forRoot(),
    DragDropModule,
    MatFormFieldModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
