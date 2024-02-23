import { TestBed } from '@angular/core/testing';

import { BOMService } from './bom.service';

describe('BOMService', () => {
  let service: BOMService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BOMService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
