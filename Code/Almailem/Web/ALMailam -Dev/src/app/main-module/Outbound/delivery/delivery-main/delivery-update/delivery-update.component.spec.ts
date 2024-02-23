import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveryUpdateComponent } from './delivery-update.component';

describe('DeliveryUpdateComponent', () => {
  let component: DeliveryUpdateComponent;
  let fixture: ComponentFixture<DeliveryUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliveryUpdateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliveryUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
