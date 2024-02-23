import { TestBed } from '@angular/core/testing';

import { VariantidService } from './variantid.service';

describe('VariantidService', () => {
  let service: VariantidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VariantidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
