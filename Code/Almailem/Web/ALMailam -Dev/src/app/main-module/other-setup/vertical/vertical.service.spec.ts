import { TestBed } from '@angular/core/testing';

import { VerticalService } from './vertical.service';

describe('VerticalService', () => {
  let service: VerticalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VerticalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
