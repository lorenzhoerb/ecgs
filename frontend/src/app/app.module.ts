import {BrowserModule} from '@angular/platform-browser';
import {DragDropModule} from '@angular/cdk/drag-drop';
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
import {RegisterComponent} from './components/register/register.component';
import {UidemoComponent} from './components/uidemo/uidemo.component';
import {ContentCardComponent} from './components/content-card/content-card.component';
import {CreateCompetitionComponent} from './components/competition/create-competition/create-competition.component';
import {UserComponent} from './components/user/user.component';
import {
  GoldenRatioContainerComponent
} from './components/containers/golden-ratio-container/golden-ratio-container.component';
import {ForgotPasswordComponent} from './components/forgot-password/forgot-password.component';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {ChangePasswordComponent} from './components/change-password/change-password.component';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatRadioModule} from '@angular/material/radio';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatSelectModule} from '@angular/material/select';
import {MatDialogModule} from '@angular/material/dialog';
import {ViewParticipantsComponent} from './components/competition/view-participants/view-participants.component';
import {ErrorComponent} from './components/error/error.component';
import {ToastrModule} from 'ngx-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormularEditorComponent} from './components/competition/formular-editor/formular-editor.component';
import {RegisterModalComponent} from './components/competition/register-modal/register-modal.component';
import {HeaderModalComponent} from './components/header-modal/header-modal.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {CompetitionListViewComponent} from './components/competition-list-view/competition-list-view.component';
import {
  CompetitionListViewEntryComponent
} from './components/competition-list-view/competition-list-view-entry/competition-list-view-entry.component';
import {
  CompetitionListViewFilterComponent
} from './components/competition-list-view/competition-list-view-filter/competition-list-view-filter.component';
import {ClubManagerImportTeamComponent} from './components/club-manager-import-team/club-manager-import-team.component';
import {
  CompetitionCalenderViewComponent
} from './components/competition-calender-view/competition-calender-view.component';
import {CompetitionHeaderComponent} from './components/competition-header/competition-header.component';
import {AutocompleteComponent} from './components/autocomplete/autocomplete.component';
import {CompetitionGradingComponent} from './components/competition/competition-grading/competition-grading.component';
import {CompetitionGradingGroupComponent}
  from './components/competition/competition-grading/competition-grading-group/competition-grading-group.component';
import {CompetitionGradingStationComponent}
  from './components/competition/competition-grading/competition-grading-station/competition-grading-station.component';
import {BulkEditorComponent} from './components/bulk-editor/bulk-editor.component';
import {ManageParticipantsComponent} from './components/competition/manage-participants/manage-participants.component';
import {RegisterToModalComponent} from './components/club-manager-edit/register-to-modal/register-to-modal.component';
import {
  SearchSelectCompetitionComponent
} from './components/club-manager-edit/register-to-modal/search-select-competition/search-select-competition.component';
import {
  ReigistrationSummaryComponent
} from './components/club-manager-edit/register-to-modal/reigistration-summary/reigistration-summary.component';
import {NavBarComponent} from './components/util/nav-bar/nav-bar.component';
import {GenderPipe} from './pipes/gender.pipe';
import {
  ViewAndEditGradingSystemComponent
} from './components/view-and-edit-grading-system/view-and-edit-grading-system.component';
import {TemplateDialogComponent} from './components/competition/template-dialog/template-dialog.component';
import {ChangeUserPictureComponent} from './components/change-user-picture/change-user-picture.component';
import {CreateCompetitionSelectGradingSystemDialogComponent}
  from './components/create-competition-select-grading-system-dialog/create-competition-select-grading-system-dialog.component';
import {ClubManagerEditComponent} from './components/club-manager-edit/club-manager-edit.component';
import {ChangeUserPassPictureComponent} from './components/change-user-pass-picture/change-user-pass-picture.component';
import {MatInputModule} from '@angular/material/input';
import {ImportFlagsComponent} from './components/import-flags/import-flags.component';
import {HeaderCreateModalComponent} from './components/header-create-modal/header-create-modal.component';
import {HeaderFolderModalComponent} from './components/header-folder-modal/header-folder-modal.component';
import { DownloadReportDialogComponent } from './components/download-report-dialog/download-report-dialog.component';
import {
  ConditionFilterInputComponent
} from './components/competition/grading-groups/condition-filter-input/condition-filter-input.component';
import {GradingGroupsComponent} from './components/competition/grading-groups/grading-groups.component';
import {
  GradingGroupDetailComponent
} from './components/competition/grading-groups/grading-group-detail/grading-group-detail.component';
import {
  GroupRegistrationDetailsComponent
} from './components/competition/grading-groups/group-registration-details/group-registration-details.component';
import { LiveResultsComponent } from './components/competition/live-results/live-results.component';
import { MyResultsComponent } from './components/my-results/my-results.component';

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
    RegisterModalComponent,
    HeaderModalComponent,
    ClubManagerImportTeamComponent,
    CompetitionCalenderViewComponent,
    CompetitionHeaderComponent,
    AutocompleteComponent,
    HeaderCreateModalComponent,
    HeaderFolderModalComponent,
    ImportFlagsComponent,
    BulkEditorComponent,
    ManageParticipantsComponent,
    ClubManagerEditComponent,
    RegisterToModalComponent,
    SearchSelectCompetitionComponent,
    ReigistrationSummaryComponent,
    CompetitionGradingComponent,
    CompetitionGradingGroupComponent,
    CompetitionGradingStationComponent,
    BulkEditorComponent,
    ManageParticipantsComponent,
    NavBarComponent,
    GenderPipe,
    TemplateDialogComponent,
    ChangeUserPictureComponent,
    ViewAndEditGradingSystemComponent,
    CreateCompetitionSelectGradingSystemDialogComponent,
    ImportFlagsComponent,
    BulkEditorComponent,
    ManageParticipantsComponent,
    ClubManagerEditComponent,
    ChangeUserPassPictureComponent,
    ConditionFilterInputComponent,
    GradingGroupsComponent,
    GradingGroupDetailComponent,
    GroupRegistrationDetailsComponent,
    GradingGroupDetailComponent,
    LiveResultsComponent,
    GradingGroupDetailComponent,
    DownloadReportDialogComponent,
    MyResultsComponent,
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
    MatRadioModule,
    MatTooltipModule,
    MatSelectModule,
    MatDialogModule,
    ToastrModule.forRoot(),
    DragDropModule,
    MatFormFieldModule,
    MatInputModule
  ],
  providers: [
    httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
