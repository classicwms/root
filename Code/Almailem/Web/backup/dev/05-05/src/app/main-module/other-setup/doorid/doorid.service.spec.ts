import { TestBed } from '@angular/core/testing';

import { DooridService } from './doorid.service';

describe('DooridService', () => {
  let service: DooridService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DooridService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
