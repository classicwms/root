import { TestBed } from '@angular/core/testing';

import { DockidService } from './dockid.service';

describe('DockidService', () => {
  let service: DockidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DockidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
