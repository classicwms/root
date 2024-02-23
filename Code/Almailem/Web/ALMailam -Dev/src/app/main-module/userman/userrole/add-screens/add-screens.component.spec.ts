import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddScreensComponent } from './add-screens.component';

describe('AddScreensComponent', () => {
  let component: AddScreensComponent;
  let fixture: ComponentFixture<AddScreensComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddScreensComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddScreensComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
