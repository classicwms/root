import { TestBed } from '@angular/core/testing';

import { HandlingequipmentidService } from './handlingequipmentid.service';

describe('HandlingequipmentidService', () => {
  let service: HandlingequipmentidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HandlingequipmentidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
