import { TestBed } from '@angular/core/testing';

import { GradingGroupService } from './grading-group.service';

describe('GradingGroupService', () => {
  let service: GradingGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GradingGroupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
