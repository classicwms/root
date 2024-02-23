import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CasenoPopupComponent } from './caseno-popup.component';

describe('CasenoPopupComponent', () => {
  let component: CasenoPopupComponent;
  let fixture: ComponentFixture<CasenoPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CasenoPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CasenoPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
