import { TestBed } from '@angular/core/testing';

import { Basicdata1Service } from './basicdata1.service';

describe('Basicdata1Service', () => {
  let service: Basicdata1Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Basicdata1Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
