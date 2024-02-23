import { TestBed } from '@angular/core/testing';

import { SpanidService } from './spanid.service';

describe('SpanidService', () => {
  let service: SpanidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpanidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
