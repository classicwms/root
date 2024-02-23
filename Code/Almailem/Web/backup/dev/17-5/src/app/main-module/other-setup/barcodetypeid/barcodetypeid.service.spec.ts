import { TestBed } from '@angular/core/testing';

import { BarcodetypeidService } from './barcodetypeid.service';

describe('BarcodetypeidService', () => {
  let service: BarcodetypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BarcodetypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
