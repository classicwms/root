import { TestBed } from '@angular/core/testing';

import { ImbatchserialService } from './imbatchserial.service';

describe('ImbatchserialService', () => {
  let service: ImbatchserialService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ImbatchserialService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
