import { TestBed } from '@angular/core/testing';

import { BillingformatService } from './billingformat.service';

describe('BillingformatService', () => {
  let service: BillingformatService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BillingformatService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
