import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveryOpenComponent } from './delivery-open.component';

describe('DeliveryOpenComponent', () => {
  let component: DeliveryOpenComponent;
  let fixture: ComponentFixture<DeliveryOpenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliveryOpenComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliveryOpenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
