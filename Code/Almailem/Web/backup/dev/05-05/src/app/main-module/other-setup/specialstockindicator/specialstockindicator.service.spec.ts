import { TestBed } from '@angular/core/testing';

import { SpecialstockindicatorService } from './specialstockindicator.service';

describe('SpecialstockindicatorService', () => {
  let service: SpecialstockindicatorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpecialstockindicatorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
