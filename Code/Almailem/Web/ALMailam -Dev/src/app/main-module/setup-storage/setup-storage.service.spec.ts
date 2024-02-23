import { TestBed } from '@angular/core/testing';

import { SetupStorageService } from './setup-storage.service';

describe('SetupStorageService', () => {
  let service: SetupStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SetupStorageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
