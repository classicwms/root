import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveryTab2Component } from './delivery-tab2.component';

describe('DeliveryTab2Component', () => {
  let component: DeliveryTab2Component;
  let fixture: ComponentFixture<DeliveryTab2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliveryTab2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliveryTab2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
