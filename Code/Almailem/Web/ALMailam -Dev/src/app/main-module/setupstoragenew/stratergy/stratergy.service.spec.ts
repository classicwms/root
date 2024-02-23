import { TestBed } from '@angular/core/testing';

import { StratergyService } from './stratergy.service';

describe('StratergyService', () => {
  let service: StratergyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StratergyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
