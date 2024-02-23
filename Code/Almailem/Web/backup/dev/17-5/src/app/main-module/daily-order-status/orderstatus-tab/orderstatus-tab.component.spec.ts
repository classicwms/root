import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderstatusTabComponent } from './orderstatus-tab.component';

describe('OrderstatusTabComponent', () => {
  let component: OrderstatusTabComponent;
  let fixture: ComponentFixture<OrderstatusTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderstatusTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrderstatusTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
