import { TestBed } from '@angular/core/testing';

import { WorkcenterService } from './workcenter.service';

describe('WorkcenterService', () => {
  let service: WorkcenterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkcenterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
