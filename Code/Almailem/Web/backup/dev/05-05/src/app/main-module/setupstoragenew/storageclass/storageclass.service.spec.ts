import { TestBed } from '@angular/core/testing';

import { StorageclassService } from './storageclass.service';

describe('StorageclassService', () => {
  let service: StorageclassService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StorageclassService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
