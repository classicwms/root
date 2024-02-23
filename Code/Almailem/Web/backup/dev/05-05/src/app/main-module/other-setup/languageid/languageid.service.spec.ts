import { TestBed } from '@angular/core/testing';

import { LanguageidService } from './languageid.service';

describe('LanguageidService', () => {
  let service: LanguageidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LanguageidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
