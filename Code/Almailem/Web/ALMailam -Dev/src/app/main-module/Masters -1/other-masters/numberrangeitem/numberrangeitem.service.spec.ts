import { TestBed } from '@angular/core/testing';

import { NumberrangeitemService } from './numberrangeitem.service';

describe('NumberrangeitemService', () => {
  let service: NumberrangeitemService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NumberrangeitemService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
