import { TestBed } from '@angular/core/testing';

import { PreoutboundService } from './preoutbound.service';

describe('PreoutboundService', () => {
  let service: PreoutboundService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PreoutboundService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
