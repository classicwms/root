import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverassignComponent } from './driverassign.component';

describe('DriverassignComponent', () => {
  let component: DriverassignComponent;
  let fixture: ComponentFixture<DriverassignComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverassignComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DriverassignComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
