import { TestBed } from '@angular/core/testing';

import { WarehousetypeService } from './warehousetype.service';

describe('WarehousetypeService', () => {
  let service: WarehousetypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WarehousetypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
