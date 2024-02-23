import { TestBed } from '@angular/core/testing';

import { StoragetypeService } from './storagetype.service';

describe('StoragetypeService', () => {
  let service: StoragetypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StoragetypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
