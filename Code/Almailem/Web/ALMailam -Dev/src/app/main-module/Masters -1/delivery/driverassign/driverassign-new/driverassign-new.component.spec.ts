import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverassignNewComponent } from './driverassign-new.component';

describe('DriverassignNewComponent', () => {
  let component: DriverassignNewComponent;
  let fixture: ComponentFixture<DriverassignNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverassignNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DriverassignNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
