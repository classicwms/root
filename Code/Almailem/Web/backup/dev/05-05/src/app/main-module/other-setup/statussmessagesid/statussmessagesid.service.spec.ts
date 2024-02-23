import { TestBed } from '@angular/core/testing';

import { StatussmessagesidService } from './statussmessagesid.service';

describe('StatussmessagesidService', () => {
  let service: StatussmessagesidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatussmessagesidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
