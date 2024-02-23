import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptUomComponent } from './receipt-uom.component';

describe('ReceiptUomComponent', () => {
  let component: ReceiptUomComponent;
  let fixture: ComponentFixture<ReceiptUomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceiptUomComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiptUomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
