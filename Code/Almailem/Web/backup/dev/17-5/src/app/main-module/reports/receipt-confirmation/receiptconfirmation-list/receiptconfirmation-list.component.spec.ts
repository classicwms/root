import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptconfirmationListComponent } from './receiptconfirmation-list.component';

describe('ReceiptconfirmationListComponent', () => {
  let component: ReceiptconfirmationListComponent;
  let fixture: ComponentFixture<ReceiptconfirmationListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceiptconfirmationListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiptconfirmationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
