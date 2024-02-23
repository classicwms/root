import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotationheaderNewComponent } from './quotationheader-new.component';

describe('QuotationheaderNewComponent', () => {
  let component: QuotationheaderNewComponent;
  let fixture: ComponentFixture<QuotationheaderNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuotationheaderNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QuotationheaderNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
