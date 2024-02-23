import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentmodeNewComponent } from './paymentmode-new.component';

describe('PaymentmodeNewComponent', () => {
  let component: PaymentmodeNewComponent;
  let fixture: ComponentFixture<PaymentmodeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymentmodeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentmodeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
