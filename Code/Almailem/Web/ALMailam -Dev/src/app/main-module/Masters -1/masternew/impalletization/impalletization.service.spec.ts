import { TestBed } from '@angular/core/testing';

import { ImpalletizationService } from './impalletization.service';

describe('ImpalletizationService', () => {
  let service: ImpalletizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ImpalletizationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
