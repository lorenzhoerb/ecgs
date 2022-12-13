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
import {MessageComponent} from './components/message/message.component';
import {CompetitionComponent} from './components/competition/competition.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { RegisterComponent } from './components/register/register.component';
import { UidemoComponent } from './components/uidemo/uidemo.component';
import { ContentCardComponent } from './components/content-card/content-card.component';
import { CreateCompetitionComponent } from './components/competition/create-competition/create-competition.component';
import { UserComponent } from './components/user/user.component';
import { GoldenRatioContainerComponent } from './components/containers/golden-ratio-container/golden-ratio-container.component';
import { TournamentHeaderComponent } from './components/tournament-header/tournament-header.component';
import { MatIconModule } from '@angular/material/icon';
import { ViewParticipantsComponent } from './components/competition/view-participants/view-participants.component';
import {ErrorComponent} from './components/error/error.component';
import {ToastrModule} from 'ngx-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { FormularEditorComponent } from './components/competition/formular-editor/formular-editor.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    CompetitionComponent,
    RegisterComponent,
    UidemoComponent,
    ContentCardComponent,
    CreateCompetitionComponent,
    UserComponent,
    GoldenRatioContainerComponent,
    TournamentHeaderComponent,
    ViewParticipantsComponent,
    ErrorComponent,
    FormularEditorComponent
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
    ToastrModule.forRoot(),
    DragDropModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
