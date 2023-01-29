import { TestBed } from '@angular/core/testing';

import { LiveResultsService } from './live-results.service';

describe('LiveResultsService', () => {
  let service: LiveResultsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LiveResultsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
