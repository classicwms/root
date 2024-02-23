import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentdispatchListComponent } from './shipmentdispatch-list.component';

describe('ShipmentdispatchListComponent', () => {
  let component: ShipmentdispatchListComponent;
  let fixture: ComponentFixture<ShipmentdispatchListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShipmentdispatchListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShipmentdispatchListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
