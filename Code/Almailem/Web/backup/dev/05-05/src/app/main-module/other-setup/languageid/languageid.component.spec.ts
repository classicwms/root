import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageidComponent } from './languageid.component';

describe('LanguageidComponent', () => {
  let component: LanguageidComponent;
  let fixture: ComponentFixture<LanguageidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LanguageidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
