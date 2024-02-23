import { TestBed } from '@angular/core/testing';

import { ImvariantService } from './imvariant.service';

describe('ImvariantService', () => {
  let service: ImvariantService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ImvariantService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
