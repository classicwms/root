import { TestBed } from '@angular/core/testing';

import { TransfertypeidService } from './transfertypeid.service';

describe('TransfertypeidService', () => {
  let service: TransfertypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransfertypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
