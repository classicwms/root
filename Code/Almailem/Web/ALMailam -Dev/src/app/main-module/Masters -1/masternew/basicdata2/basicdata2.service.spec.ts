import { TestBed } from '@angular/core/testing';

import { Basicdata2Service } from './basicdata2.service';

describe('Basicdata2Service', () => {
  let service: Basicdata2Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Basicdata2Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
