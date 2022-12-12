import {Component, Input, OnInit, OnChanges, SimpleChanges} from '@angular/core';
import {ListError} from '../../dtos/list-error';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit, OnChanges {
  @Input() title = 'Titel...';
  @Input() error: Error = null;
  errMsg: ListError = {
    message: '',
    errors: null
  };

  constructor() {}

  ngOnInit(): void {}

  ngOnChanges(changes: SimpleChanges): void {
    const nextErr: any = changes['error'].currentValue;

    if(nextErr.error.errors !== undefined) {
      this.errMsg.errors = nextErr.error.errors;
      this.error.message = nextErr.error.message;
      return;
    }

    console.log(nextErr.error);
    this.errMsg.message = nextErr.error;
  }

  vanishError(): void {
    this.error = null;
  }
}
