import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicreportComponent } from './periodicreport.component';

describe('PeriodicreportComponent', () => {
  let component: PeriodicreportComponent;
  let fixture: ComponentFixture<PeriodicreportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PeriodicreportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodicreportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
