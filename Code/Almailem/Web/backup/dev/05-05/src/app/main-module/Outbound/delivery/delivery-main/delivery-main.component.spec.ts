import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveryMainComponent } from './delivery-main.component';

describe('DeliveryMainComponent', () => {
  let component: DeliveryMainComponent;
  let fixture: ComponentFixture<DeliveryMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliveryMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliveryMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
