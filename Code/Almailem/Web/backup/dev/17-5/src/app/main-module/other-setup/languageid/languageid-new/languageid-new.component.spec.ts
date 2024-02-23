import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageidNewComponent } from './languageid-new.component';

describe('LanguageidNewComponent', () => {
  let component: LanguageidNewComponent;
  let fixture: ComponentFixture<LanguageidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LanguageidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
