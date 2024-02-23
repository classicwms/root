import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuccessfulOrdersComponent } from './successful-orders.component';

describe('SuccessfulOrdersComponent', () => {
  let component: SuccessfulOrdersComponent;
  let fixture: ComponentFixture<SuccessfulOrdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SuccessfulOrdersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuccessfulOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
