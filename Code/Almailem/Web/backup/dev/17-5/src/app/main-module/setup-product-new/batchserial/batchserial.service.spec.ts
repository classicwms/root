import { TestBed } from '@angular/core/testing';

import { BatchserialService } from './batchserial.service';

describe('BatchserialService', () => {
  let service: BatchserialService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BatchserialService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
