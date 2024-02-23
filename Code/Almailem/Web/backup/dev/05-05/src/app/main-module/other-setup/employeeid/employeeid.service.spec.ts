import { TestBed } from '@angular/core/testing';

import { EmployeeidService } from './employeeid.service';

describe('EmployeeidService', () => {
  let service: EmployeeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmployeeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
