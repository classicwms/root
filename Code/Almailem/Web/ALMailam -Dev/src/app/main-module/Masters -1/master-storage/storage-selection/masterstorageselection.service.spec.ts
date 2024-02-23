import { TestBed } from '@angular/core/testing';

import { MasterstorageselectionService } from './masterstorageselection.service';

describe('MasterstorageselectionService', () => {
  let service: MasterstorageselectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MasterstorageselectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
