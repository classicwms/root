import { TestBed } from '@angular/core/testing';

import { BasicdataService } from './basicdata.service';

describe('BasicdataService', () => {
  let service: BasicdataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BasicdataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
