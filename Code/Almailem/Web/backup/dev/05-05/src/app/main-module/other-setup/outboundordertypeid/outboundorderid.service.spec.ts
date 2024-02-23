import { TestBed } from '@angular/core/testing';

import { OutboundorderidService } from './outboundorderid.service';

describe('OutboundorderidService', () => {
  let service: OutboundorderidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OutboundorderidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
