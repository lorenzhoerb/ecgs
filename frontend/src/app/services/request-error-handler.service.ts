import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class RequestErrorHandlerService {

  constructor(
    private toastr: ToastrService
  ) { }

  public defaultErrorhandle(err: any) {
    console.log(err);
    if (err.status === 0) {
      this.toastr.error('Could not connect to the server', 'Connection error');
    } else if (err.status === 409) {
      this.toastr.error(err.error.message);
    } else if ([422, 403].includes(err.status)) {
      this.toastr.error(
        `<ul>${err.error.errors.map(e => '<li>' + e + '</li>').join('\n')}</ul>`,
        err.error.message,
        { enableHtml: true });
    } else if ([409, 404].includes(err.status)) {
      this.toastr.error(err.error.message);
    } else if (typeof err.error === 'string') {
      this.toastr.error(err.error);
    }
  }
}
