import { TestBed } from '@angular/core/testing';

import { ProcessidService } from './processid.service';

describe('ProcessidService', () => {
  let service: ProcessidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProcessidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
