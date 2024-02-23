import { TestBed } from '@angular/core/testing';

import { ControlprocessidService } from './controlprocessid.service';

describe('ControlprocessidService', () => {
  let service: ControlprocessidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ControlprocessidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
