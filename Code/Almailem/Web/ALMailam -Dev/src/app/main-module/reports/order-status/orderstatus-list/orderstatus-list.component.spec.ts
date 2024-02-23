import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderstatusListComponent } from './orderstatus-list.component';

describe('OrderstatusListComponent', () => {
  let component: OrderstatusListComponent;
  let fixture: ComponentFixture<OrderstatusListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderstatusListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrderstatusListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
