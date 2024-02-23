import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdermanagementMainComponent } from './ordermanagement-main.component';

describe('OrdermanagementMainComponent', () => {
  let component: OrdermanagementMainComponent;
  let fixture: ComponentFixture<OrdermanagementMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrdermanagementMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrdermanagementMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
