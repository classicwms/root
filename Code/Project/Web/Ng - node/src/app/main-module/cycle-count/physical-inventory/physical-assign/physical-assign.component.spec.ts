import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhysicalAssignComponent } from './physical-assign.component';

describe('PhysicalAssignComponent', () => {
  let component: PhysicalAssignComponent;
  let fixture: ComponentFixture<PhysicalAssignComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PhysicalAssignComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PhysicalAssignComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
