import { TestBed } from '@angular/core/testing';

import { PrepetualCountService } from './prepetual-count.service';

describe('PrepetualCountService', () => {
  let service: PrepetualCountService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrepetualCountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
