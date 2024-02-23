import { TestBed } from '@angular/core/testing';

import { ImpackingService } from './impacking.service';

describe('ImpackingService', () => {
  let service: ImpackingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ImpackingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
