import { TestBed } from '@angular/core/testing';

import { HhtuserService } from './hhtuser.service';

describe('HhtuserService', () => {
  let service: HhtuserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HhtuserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
