import { TestBed } from '@angular/core/testing';

import { BinlocationService } from './binlocation.service';

describe('BinlocationService', () => {
  let service: BinlocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BinlocationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
