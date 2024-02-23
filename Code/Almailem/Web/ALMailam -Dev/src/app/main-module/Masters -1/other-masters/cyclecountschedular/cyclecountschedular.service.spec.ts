import { TestBed } from '@angular/core/testing';

import { CyclecountschedularService } from './cyclecountschedular.service';

describe('CyclecountschedularService', () => {
  let service: CyclecountschedularService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CyclecountschedularService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
