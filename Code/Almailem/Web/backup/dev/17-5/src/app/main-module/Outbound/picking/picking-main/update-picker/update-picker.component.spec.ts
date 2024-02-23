import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatePickerComponent } from './update-picker.component';

describe('UpdatePickerComponent', () => {
  let component: UpdatePickerComponent;
  let fixture: ComponentFixture<UpdatePickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdatePickerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdatePickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
