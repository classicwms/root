import { TestBed } from '@angular/core/testing';

import { ProformainvoicelineService } from './proformainvoiceline.service';

describe('ProformainvoicelineService', () => {
  let service: ProformainvoicelineService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProformainvoicelineService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
