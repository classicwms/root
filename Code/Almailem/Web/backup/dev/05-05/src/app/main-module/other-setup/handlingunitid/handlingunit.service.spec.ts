import { TestBed } from '@angular/core/testing';

import { HandlingunitService } from './handlingunit.service';

describe('HandlingunitService', () => {
  let service: HandlingunitService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HandlingunitService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
