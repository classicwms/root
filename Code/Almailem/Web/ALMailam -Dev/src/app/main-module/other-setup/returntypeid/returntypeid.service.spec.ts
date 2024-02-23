import { TestBed } from '@angular/core/testing';

import { ReturntypeidService } from './returntypeid.service';

describe('ReturntypeidService', () => {
  let service: ReturntypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReturntypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
