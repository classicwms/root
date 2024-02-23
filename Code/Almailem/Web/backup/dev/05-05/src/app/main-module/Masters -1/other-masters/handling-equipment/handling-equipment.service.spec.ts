import { TestBed } from '@angular/core/testing';

import { HandlingEquipmentService } from './handling-equipment.service';

describe('HandlingEquipmentService', () => {
  let service: HandlingEquipmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HandlingEquipmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
