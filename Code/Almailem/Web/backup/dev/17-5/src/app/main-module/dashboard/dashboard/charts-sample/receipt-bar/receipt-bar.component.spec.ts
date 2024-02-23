import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptBarComponent } from './receipt-bar.component';

describe('ReceiptBarComponent', () => {
  let component: ReceiptBarComponent;
  let fixture: ComponentFixture<ReceiptBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceiptBarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiptBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
