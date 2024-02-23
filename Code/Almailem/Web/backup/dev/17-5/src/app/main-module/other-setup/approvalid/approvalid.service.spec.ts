import { TestBed } from '@angular/core/testing';

import { ApprovalidService } from './approvalid.service';

describe('ApprovalidService', () => {
  let service: ApprovalidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApprovalidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
