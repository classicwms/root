import { TestBed } from '@angular/core/testing';

import { OutboundsucessordersService } from './outboundsucessorders.service';

describe('OutboundsucessordersService', () => {
  let service: OutboundsucessordersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OutboundsucessordersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
