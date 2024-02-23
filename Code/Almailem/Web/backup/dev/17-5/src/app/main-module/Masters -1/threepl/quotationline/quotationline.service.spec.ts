import { TestBed } from '@angular/core/testing';

import { QuotationlineService } from './quotationline.service';

describe('QuotationlineService', () => {
  let service: QuotationlineService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuotationlineService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
