import { TestBed } from '@angular/core/testing';

import { InvoicelinesService } from './invoicelines.service';

describe('InvoicelinesService', () => {
  let service: InvoicelinesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InvoicelinesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
