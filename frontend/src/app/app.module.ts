import {BrowserModule} from '@angular/platform-browser';
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
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { RegisterComponent } from './components/register/register.component';
import { UidemoComponent } from './components/uidemo/uidemo.component';
import { ContentCardComponent } from './components/content-card/content-card.component';
import { UserComponent } from './components/user/user.component';
import { GoldenRatioContainerComponent } from './components/containers/golden-ratio-container/golden-ratio-container.component';
import { TournamentHeaderComponent } from './components/tournament-header/tournament-header.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    RegisterComponent,
    UidemoComponent,
    ContentCardComponent,
    UserComponent,
    GoldenRatioContainerComponent,
    TournamentHeaderComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
