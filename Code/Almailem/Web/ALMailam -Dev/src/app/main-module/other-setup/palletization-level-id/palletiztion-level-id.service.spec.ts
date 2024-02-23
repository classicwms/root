import { TestBed } from '@angular/core/testing';

import { PalletiztionLevelIdService } from './palletiztion-level-id.service';

describe('PalletiztionLevelIdService', () => {
  let service: PalletiztionLevelIdService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PalletiztionLevelIdService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
