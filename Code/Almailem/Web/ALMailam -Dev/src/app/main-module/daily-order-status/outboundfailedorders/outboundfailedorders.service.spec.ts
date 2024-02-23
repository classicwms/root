import { TestBed } from '@angular/core/testing';

import { OutboundfailedordersService } from './outboundfailedorders.service';

describe('OutboundfailedordersService', () => {
  let service: OutboundfailedordersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OutboundfailedordersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
