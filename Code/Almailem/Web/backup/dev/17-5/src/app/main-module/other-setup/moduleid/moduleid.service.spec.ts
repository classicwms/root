import { TestBed } from '@angular/core/testing';

import { ModuleidService } from './moduleid.service';

describe('ModuleidService', () => {
  let service: ModuleidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModuleidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
