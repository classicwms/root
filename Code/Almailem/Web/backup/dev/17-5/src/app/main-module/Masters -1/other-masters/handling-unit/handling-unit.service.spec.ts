import { TestBed } from '@angular/core/testing';

import { HandlingUnitService } from './handling-unit.service';

describe('HandlingUnitService', () => {
  let service: HandlingUnitService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HandlingUnitService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
