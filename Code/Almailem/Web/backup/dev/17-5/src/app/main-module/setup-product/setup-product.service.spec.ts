import { TestBed } from '@angular/core/testing';

import { SetupProductService } from './setup-product.service';

describe('SetupProductService', () => {
  let service: SetupProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SetupProductService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
