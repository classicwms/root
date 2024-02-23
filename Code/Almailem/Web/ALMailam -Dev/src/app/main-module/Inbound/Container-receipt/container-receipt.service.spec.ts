import { TestBed } from '@angular/core/testing';

import { ContainerReceiptService } from './container-receipt.service';

describe('ContainerReceiptService', () => {
  let service: ContainerReceiptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContainerReceiptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
