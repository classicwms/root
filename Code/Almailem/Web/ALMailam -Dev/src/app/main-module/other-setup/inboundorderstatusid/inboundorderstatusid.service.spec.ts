import { TestBed } from '@angular/core/testing';

import { InboundorderstatusidService } from './inboundorderstatusid.service';

describe('InboundorderstatusidService', () => {
  let service: InboundorderstatusidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InboundorderstatusidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
