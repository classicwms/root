import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmEditComponent } from './confirm-edit.component';

describe('ConfirmEditComponent', () => {
  let component: ConfirmEditComponent;
  let fixture: ComponentFixture<ConfirmEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfirmEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
