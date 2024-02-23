import { TestBed } from '@angular/core/testing';

import { MenuNewService } from './menu-new.service';

describe('MenuNewService', () => {
  let service: MenuNewService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MenuNewService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
