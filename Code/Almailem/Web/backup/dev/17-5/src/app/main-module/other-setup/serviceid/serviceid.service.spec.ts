import { TestBed } from '@angular/core/testing';

import { ServiceidService } from './serviceid.service';

describe('ServiceidService', () => {
  let service: ServiceidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServiceidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
