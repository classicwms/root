import { TestBed } from '@angular/core/testing';

import { StoragebintypeService } from './storagebintype.service';

describe('StoragebintypeService', () => {
  let service: StoragebintypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StoragebintypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
