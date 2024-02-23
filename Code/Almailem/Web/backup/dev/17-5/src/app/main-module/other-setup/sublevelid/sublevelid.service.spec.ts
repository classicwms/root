import { TestBed } from '@angular/core/testing';

import { SublevelidService } from './sublevelid.service';

describe('SublevelidService', () => {
  let service: SublevelidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SublevelidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
