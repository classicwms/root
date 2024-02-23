import { TestBed } from '@angular/core/testing';

import { InboundordertypeidService } from './inboundordertypeid.service';

describe('InboundordertypeidService', () => {
  let service: InboundordertypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InboundordertypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
