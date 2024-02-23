import { TestBed } from '@angular/core/testing';

import { BarcodesubtypeidService } from './barcodesubtypeid.service';

describe('BarcodesubtypeidService', () => {
  let service: BarcodesubtypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BarcodesubtypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
