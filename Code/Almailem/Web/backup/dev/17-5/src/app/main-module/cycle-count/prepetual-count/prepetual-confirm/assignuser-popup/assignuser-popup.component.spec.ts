import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignuserPopupComponent } from './assignuser-popup.component';

describe('AssignuserPopupComponent', () => {
  let component: AssignuserPopupComponent;
  let fixture: ComponentFixture<AssignuserPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignuserPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignuserPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
