import { TestBed } from '@angular/core/testing';

import { AltuomService } from './altuom.service';

describe('AltuomService', () => {
  let service: AltuomService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AltuomService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
