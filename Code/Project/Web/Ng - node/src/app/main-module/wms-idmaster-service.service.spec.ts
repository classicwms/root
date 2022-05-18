import { TestBed } from '@angular/core/testing';

import { WmsIdmasterServiceService } from './wms-idmaster-service.service';

describe('WmsIdmasterServiceService', () => {
  let service: WmsIdmasterServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WmsIdmasterServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
