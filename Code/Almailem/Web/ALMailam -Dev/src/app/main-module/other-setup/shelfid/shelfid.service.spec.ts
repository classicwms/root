import { TestBed } from '@angular/core/testing';

import { ShelfidService } from './shelfid.service';

describe('ShelfidService', () => {
  let service: ShelfidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShelfidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
