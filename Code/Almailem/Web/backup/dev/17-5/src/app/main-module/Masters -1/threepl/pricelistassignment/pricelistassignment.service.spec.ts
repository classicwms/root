import { TestBed } from '@angular/core/testing';

import { PricelistassignmentService } from './pricelistassignment.service';

describe('PricelistassignmentService', () => {
  let service: PricelistassignmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PricelistassignmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
