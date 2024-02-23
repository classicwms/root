import { TestBed } from '@angular/core/testing';

import { SetupSelectionService } from './setup-selection.service';

describe('SetupSelectionService', () => {
  let service: SetupSelectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SetupSelectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
