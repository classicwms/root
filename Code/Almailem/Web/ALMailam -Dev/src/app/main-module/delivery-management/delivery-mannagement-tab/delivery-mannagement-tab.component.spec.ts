import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveryMannagementTabComponent } from './delivery-mannagement-tab.component';

describe('DeliveryMannagementTabComponent', () => {
  let component: DeliveryMannagementTabComponent;
  let fixture: ComponentFixture<DeliveryMannagementTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliveryMannagementTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliveryMannagementTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
