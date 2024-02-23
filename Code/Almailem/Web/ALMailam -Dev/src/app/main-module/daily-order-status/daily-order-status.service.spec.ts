import { TestBed } from '@angular/core/testing';

import { DailyOrderStatusService } from './daily-order-status.service';

describe('DailyOrderStatusService', () => {
  let service: DailyOrderStatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DailyOrderStatusService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
