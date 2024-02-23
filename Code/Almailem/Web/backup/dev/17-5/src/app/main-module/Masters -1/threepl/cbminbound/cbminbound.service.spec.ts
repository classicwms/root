import { TestBed } from '@angular/core/testing';

import { CbminboundService } from './cbminbound.service';

describe('CbminboundService', () => {
  let service: CbminboundService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CbminboundService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
