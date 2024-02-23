import { TestBed } from '@angular/core/testing';

import { MovementtypeidService } from './movementtypeid.service';

describe('MovementtypeidService', () => {
  let service: MovementtypeidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MovementtypeidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
