import { TestBed } from '@angular/core/testing';

import { DriverassignService } from './driverassign.service';

describe('DriverassignService', () => {
  let service: DriverassignService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverassignService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
