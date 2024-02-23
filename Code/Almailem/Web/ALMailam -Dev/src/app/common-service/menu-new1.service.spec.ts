import { TestBed } from '@angular/core/testing';

import { MenuNew1Service } from './menu-new1.service';

describe('MenuNew1Service', () => {
  let service: MenuNew1Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MenuNew1Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
