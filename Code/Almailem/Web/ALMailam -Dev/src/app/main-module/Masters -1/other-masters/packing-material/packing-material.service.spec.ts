import { TestBed } from '@angular/core/testing';

import { PackingMaterialService } from './packing-material.service';

describe('PackingMaterialService', () => {
  let service: PackingMaterialService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PackingMaterialService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
