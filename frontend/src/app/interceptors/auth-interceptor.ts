import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private globals: Globals) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authUri = [this.globals.backendUri + '/authentication', this.globals.backendUri + '/registration'];
    //console.log("TEST:::" + authUri);

    // Do not intercept authentication requests
    if (authUri.includes(req.url)) {
      return next.handle(req);
    }

    //Do not intercept if no token is present
    if (!this.authService.getToken()) {
      return next.handle(req);
    }

    const authReq = req.clone({
      headers: req.headers.set('Authorization', this.authService.getToken())
    });

    return next.handle(authReq);
  }
}
