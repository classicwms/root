import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HhtPickingReportComponent } from './hht-picking-report.component';

describe('HhtPickingReportComponent', () => {
  let component: HhtPickingReportComponent;
  let fixture: ComponentFixture<HhtPickingReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HhtPickingReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HhtPickingReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
