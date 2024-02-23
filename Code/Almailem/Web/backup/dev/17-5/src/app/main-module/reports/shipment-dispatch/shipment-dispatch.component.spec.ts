import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentDispatchComponent } from './shipment-dispatch.component';

describe('ShipmentDispatchComponent', () => {
  let component: ShipmentDispatchComponent;
  let fixture: ComponentFixture<ShipmentDispatchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShipmentDispatchComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShipmentDispatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
