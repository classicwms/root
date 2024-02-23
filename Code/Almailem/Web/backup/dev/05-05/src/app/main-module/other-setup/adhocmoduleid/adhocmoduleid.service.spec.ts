import { TestBed } from '@angular/core/testing';

import { AdhocmoduleidService } from './adhocmoduleid.service';

describe('AdhocmoduleidService', () => {
  let service: AdhocmoduleidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdhocmoduleidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
