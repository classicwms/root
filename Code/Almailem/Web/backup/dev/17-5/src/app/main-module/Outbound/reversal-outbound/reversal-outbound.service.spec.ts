import { TestBed } from '@angular/core/testing';

import { ReversalOutboundService } from './reversal-outbound.service';

describe('ReversalOutboundService', () => {
  let service: ReversalOutboundService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReversalOutboundService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
