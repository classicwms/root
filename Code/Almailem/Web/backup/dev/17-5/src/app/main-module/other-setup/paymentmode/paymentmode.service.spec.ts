import { TestBed } from '@angular/core/testing';

import { PaymentmodeService } from './paymentmode.service';

describe('PaymentmodeService', () => {
  let service: PaymentmodeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentmodeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
