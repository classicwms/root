import { TestBed } from '@angular/core/testing';

import { ControltypeidService } from './controltypeid.service';

describe('ControltypeidService', () => {
  let service: ControltypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ControltypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
