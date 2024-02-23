import { TestBed } from '@angular/core/testing';

import { MasterproductselectionService } from './masterproductselection.service';

describe('MasterproductselectionService', () => {
  let service: MasterproductselectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MasterproductselectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
