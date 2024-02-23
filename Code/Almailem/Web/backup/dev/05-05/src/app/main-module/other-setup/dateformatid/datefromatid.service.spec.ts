import { TestBed } from '@angular/core/testing';

import { DatefromatidService } from './datefromatid.service';

describe('DatefromatidService', () => {
  let service: DatefromatidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DatefromatidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
