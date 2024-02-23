import { TestBed } from '@angular/core/testing';

import { CyclecounttypeidService } from './cyclecounttypeid.service';

describe('CyclecounttypeidService', () => {
  let service: CyclecounttypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CyclecounttypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
