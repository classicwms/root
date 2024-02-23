import { TestBed } from '@angular/core/testing';

import { AisleService } from './aisle.service';

describe('AisleService', () => {
  let service: AisleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AisleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
