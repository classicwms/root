import { TestBed } from '@angular/core/testing';

import { StatusidService } from './statusid.service';

describe('StatusidService', () => {
  let service: StatusidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatusidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
