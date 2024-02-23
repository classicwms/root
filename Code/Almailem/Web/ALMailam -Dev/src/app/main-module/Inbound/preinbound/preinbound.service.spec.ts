import { TestBed } from '@angular/core/testing';

import { PreinboundService } from './preinbound.service';

describe('PreinboundService', () => {
  let service: PreinboundService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PreinboundService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
