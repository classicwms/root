import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveryTabComponent } from './delivery-tab.component';

describe('DeliveryTabComponent', () => {
  let component: DeliveryTabComponent;
  let fixture: ComponentFixture<DeliveryTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliveryTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliveryTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
