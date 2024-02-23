import { TestBed } from '@angular/core/testing';

import { ImcapacityService } from './imcapacity.service';

describe('ImcapacityService', () => {
  let service: ImcapacityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ImcapacityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
