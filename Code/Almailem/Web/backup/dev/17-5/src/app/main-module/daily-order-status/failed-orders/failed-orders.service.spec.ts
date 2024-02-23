import { TestBed } from '@angular/core/testing';

import { FailedOrdersService } from './failed-orders.service';

describe('FailedOrdersService', () => {
  let service: FailedOrdersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FailedOrdersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
