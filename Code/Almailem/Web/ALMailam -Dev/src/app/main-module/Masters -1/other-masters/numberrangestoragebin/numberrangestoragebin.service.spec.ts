import { TestBed } from '@angular/core/testing';

import { NumberrangestoragebinService } from './numberrangestoragebin.service';

describe('NumberrangestoragebinService', () => {
  let service: NumberrangestoragebinService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NumberrangestoragebinService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
