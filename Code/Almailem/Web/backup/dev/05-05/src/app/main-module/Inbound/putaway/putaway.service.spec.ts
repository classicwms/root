import { TestBed } from '@angular/core/testing';

import { PutawayService } from './putaway.service';

describe('PutawayService', () => {
  let service: PutawayService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PutawayService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
