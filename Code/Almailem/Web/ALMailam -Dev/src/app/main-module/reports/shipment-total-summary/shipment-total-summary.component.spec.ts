import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentTotalSummaryComponent } from './shipment-total-summary.component';

describe('ShipmentTotalSummaryComponent', () => {
  let component: ShipmentTotalSummaryComponent;
  let fixture: ComponentFixture<ShipmentTotalSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShipmentTotalSummaryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShipmentTotalSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
