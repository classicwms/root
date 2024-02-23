import { TestBed } from '@angular/core/testing';

import { NumberrangeService } from './numberrange.service';

describe('NumberrangeService', () => {
  let service: NumberrangeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NumberrangeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
