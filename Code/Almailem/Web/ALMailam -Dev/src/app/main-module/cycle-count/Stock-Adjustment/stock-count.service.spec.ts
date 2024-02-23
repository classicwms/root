import { TestBed } from '@angular/core/testing';

import { StockCountService } from './stock-count.service';

describe('StockCountService', () => {
  let service: StockCountService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StockCountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
