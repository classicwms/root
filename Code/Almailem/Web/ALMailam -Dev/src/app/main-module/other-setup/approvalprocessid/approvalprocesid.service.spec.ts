import { TestBed } from '@angular/core/testing';

import { ApprovalprocesidService } from './approvalprocesid.service';

describe('ApprovalprocesidService', () => {
  let service: ApprovalprocesidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApprovalprocesidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
