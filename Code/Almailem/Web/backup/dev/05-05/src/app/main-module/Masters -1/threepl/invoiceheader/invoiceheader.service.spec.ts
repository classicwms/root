import { TestBed } from '@angular/core/testing';

import { InvoiceheaderService } from './invoiceheader.service';

describe('InvoiceheaderService', () => {
  let service: InvoiceheaderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InvoiceheaderService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
