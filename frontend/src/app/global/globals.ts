import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Globals {
  backendUri: string = this.findBackendUrl();
  websocketPollFrequency: number = 2 * 60 * 1000;

  public findBackendUrl(): string {
    if (window.location.port === '4200') { // local `ng serve`, backend at localhost:8080
      return 'http://localhost:8080/api/v1';
    } else {
      // assume deployed somewhere and backend is available at same host/port as frontend
      return window.location.protocol + '//' + window.location.host + window.location.pathname + 'api/v1';
    }
  }
}


