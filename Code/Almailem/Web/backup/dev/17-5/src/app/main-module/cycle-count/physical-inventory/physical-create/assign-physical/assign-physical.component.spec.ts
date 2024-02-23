import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignPhysicalComponent } from './assign-physical.component';

describe('AssignPhysicalComponent', () => {
  let component: AssignPhysicalComponent;
  let fixture: ComponentFixture<AssignPhysicalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignPhysicalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignPhysicalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
