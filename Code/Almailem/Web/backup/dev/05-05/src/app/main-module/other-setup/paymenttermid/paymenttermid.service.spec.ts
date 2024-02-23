import { TestBed } from '@angular/core/testing';

import { PaymenttermidService } from './paymenttermid.service';

describe('PaymenttermidService', () => {
  let service: PaymenttermidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymenttermidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
