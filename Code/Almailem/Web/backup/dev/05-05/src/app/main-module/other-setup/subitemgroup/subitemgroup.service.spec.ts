import { TestBed } from '@angular/core/testing';

import { SubitemgroupService } from './subitemgroup.service';

describe('SubitemgroupService', () => {
  let service: SubitemgroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubitemgroupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
