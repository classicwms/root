import { TestBed } from '@angular/core/testing';

import { MasterproductService } from './masterproduct.service';

describe('MasterproductService', () => {
  let service: MasterproductService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MasterproductService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
