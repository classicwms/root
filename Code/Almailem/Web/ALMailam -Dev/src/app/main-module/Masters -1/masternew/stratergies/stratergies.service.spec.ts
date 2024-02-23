import { TestBed } from '@angular/core/testing';

import { StratergiesService } from './stratergies.service';

describe('StratergiesService', () => {
  let service: StratergiesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StratergiesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
