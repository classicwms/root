import { TestBed } from '@angular/core/testing';

import { StorageSectionService } from './storage-section.service';

describe('StorageSectionService', () => {
  let service: StorageSectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StorageSectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
