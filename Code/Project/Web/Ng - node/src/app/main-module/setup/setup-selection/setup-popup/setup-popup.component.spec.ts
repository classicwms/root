import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupPopupComponent } from './setup-popup.component';

describe('SetupPopupComponent', () => {
  let component: SetupPopupComponent;
  let fixture: ComponentFixture<SetupPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SetupPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SetupPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
