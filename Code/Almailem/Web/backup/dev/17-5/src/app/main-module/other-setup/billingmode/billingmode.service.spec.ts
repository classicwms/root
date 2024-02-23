import { TestBed } from '@angular/core/testing';

import { BillingmodeService } from './billingmode.service';

describe('BillingmodeService', () => {
  let service: BillingmodeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BillingmodeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
