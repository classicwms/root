import { TestBed } from '@angular/core/testing';

import { OutboundorderstatusidService } from './outboundorderstatusid.service';

describe('OutboundorderstatusidService', () => {
  let service: OutboundorderstatusidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OutboundorderstatusidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
