import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentorderComponent } from './shipmentorder.component';

describe('ShipmentorderComponent', () => {
  let component: ShipmentorderComponent;
  let fixture: ComponentFixture<ShipmentorderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShipmentorderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShipmentorderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
