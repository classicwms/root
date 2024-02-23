import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupSelectionComponent } from './setup-selection.component';

describe('SetupSelectionComponent', () => {
  let component: SetupSelectionComponent;
  let fixture: ComponentFixture<SetupSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SetupSelectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SetupSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
