import { TestBed } from '@angular/core/testing';

import { BinsectionidService } from './binsectionid.service';

describe('BinsectionidService', () => {
  let service: BinsectionidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BinsectionidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
