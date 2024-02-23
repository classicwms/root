import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupgrconfirmComponent } from './popupgrconfirm.component';

describe('PopupgrconfirmComponent', () => {
  let component: PopupgrconfirmComponent;
  let fixture: ComponentFixture<PopupgrconfirmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupgrconfirmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PopupgrconfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
