import { TestBed } from '@angular/core/testing';

import { SubmovementtypeidService } from './submovementtypeid.service';

describe('SubmovementtypeidService', () => {
  let service: SubmovementtypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubmovementtypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
