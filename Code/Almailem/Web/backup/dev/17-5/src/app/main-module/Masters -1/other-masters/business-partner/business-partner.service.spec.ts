import { TestBed } from '@angular/core/testing';

import { BusinessPartnerService } from './business-partner.service';

describe('BusinessPartnerService', () => {
  let service: BusinessPartnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BusinessPartnerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
