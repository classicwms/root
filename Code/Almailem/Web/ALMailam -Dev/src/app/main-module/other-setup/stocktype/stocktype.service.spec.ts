import { TestBed } from '@angular/core/testing';

import { StocktypeService } from './stocktype.service';

describe('StocktypeService', () => {
  let service: StocktypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StocktypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
