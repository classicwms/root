import { TestBed } from '@angular/core/testing';

import { StocksampleService } from './stocksample.service';

describe('StocksampleService', () => {
  let service: StocksampleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StocksampleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
