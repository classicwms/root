import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptConfirmationComponent } from './receipt-confirmation.component';

describe('ReceiptConfirmationComponent', () => {
  let component: ReceiptConfirmationComponent;
  let fixture: ComponentFixture<ReceiptConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceiptConfirmationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiptConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
