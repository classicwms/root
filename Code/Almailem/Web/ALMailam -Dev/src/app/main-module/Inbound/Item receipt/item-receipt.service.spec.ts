import { TestBed } from '@angular/core/testing';

import { ItemReceiptService } from './item-receipt.service';

describe('ItemReceiptService', () => {
  let service: ItemReceiptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItemReceiptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
