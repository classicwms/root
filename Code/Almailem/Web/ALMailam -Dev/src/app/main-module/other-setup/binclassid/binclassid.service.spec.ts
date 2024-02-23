import { TestBed } from '@angular/core/testing';

import { BinclassidService } from './binclassid.service';

describe('BinclassidService', () => {
  let service: BinclassidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BinclassidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
