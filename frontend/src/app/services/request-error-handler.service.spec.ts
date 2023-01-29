import { TestBed } from '@angular/core/testing';

import { RequestErrorHandlerService } from './request-error-handler.service';

describe('RequestErrorHandlerService', () => {
  let service: RequestErrorHandlerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RequestErrorHandlerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
