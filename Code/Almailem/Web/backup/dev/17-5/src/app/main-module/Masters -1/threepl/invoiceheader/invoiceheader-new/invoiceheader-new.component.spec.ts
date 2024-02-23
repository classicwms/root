import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceheaderNewComponent } from './invoiceheader-new.component';

describe('InvoiceheaderNewComponent', () => {
  let component: InvoiceheaderNewComponent;
  let fixture: ComponentFixture<InvoiceheaderNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InvoiceheaderNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvoiceheaderNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
