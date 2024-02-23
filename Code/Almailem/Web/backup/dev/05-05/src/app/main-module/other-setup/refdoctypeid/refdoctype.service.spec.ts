import { TestBed } from '@angular/core/testing';

import { RefdoctypeService } from './refdoctype.service';

describe('RefdoctypeService', () => {
  let service: RefdoctypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RefdoctypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
