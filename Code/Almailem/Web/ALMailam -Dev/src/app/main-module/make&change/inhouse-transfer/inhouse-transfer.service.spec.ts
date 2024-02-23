import { TestBed } from '@angular/core/testing';

import { InhouseTransferService } from './inhouse-transfer.service';

describe('InhouseTransferService', () => {
  let service: InhouseTransferService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InhouseTransferService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
