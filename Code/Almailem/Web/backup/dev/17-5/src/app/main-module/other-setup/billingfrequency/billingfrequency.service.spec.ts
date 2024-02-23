import { TestBed } from '@angular/core/testing';

import { BillingfrequencyService } from './billingfrequency.service';

describe('BillingfrequencyService', () => {
  let service: BillingfrequencyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BillingfrequencyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
