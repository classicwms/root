import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingReportComponent } from './picking-report.component';

describe('PickingReportComponent', () => {
  let component: PickingReportComponent;
  let fixture: ComponentFixture<PickingReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
