import { TestBed } from '@angular/core/testing';

import { AltpartService } from './altpart.service';

describe('AltpartService', () => {
  let service: AltpartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AltpartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
