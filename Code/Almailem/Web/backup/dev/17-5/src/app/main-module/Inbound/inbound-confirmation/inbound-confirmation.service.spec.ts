import { TestBed } from '@angular/core/testing';

import { InboundConfirmationService } from './inbound-confirmation.service';

describe('InboundConfirmationService', () => {
  let service: InboundConfirmationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InboundConfirmationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
