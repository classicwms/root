import { TestBed } from '@angular/core/testing';

import { DecimalnotationidService } from './decimalnotationid.service';

describe('DecimalnotationidService', () => {
  let service: DecimalnotationidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DecimalnotationidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
