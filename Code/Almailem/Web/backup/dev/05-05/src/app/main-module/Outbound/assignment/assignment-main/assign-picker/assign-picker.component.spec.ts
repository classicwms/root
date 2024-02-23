import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignPickerComponent } from './assign-picker.component';

describe('AssignPickerComponent', () => {
  let component: AssignPickerComponent;
  let fixture: ComponentFixture<AssignPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignPickerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignPickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
