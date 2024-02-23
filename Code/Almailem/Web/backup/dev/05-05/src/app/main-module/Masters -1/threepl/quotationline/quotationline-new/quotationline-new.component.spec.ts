import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotationlineNewComponent } from './quotationline-new.component';

describe('QuotationlineNewComponent', () => {
  let component: QuotationlineNewComponent;
  let fixture: ComponentFixture<QuotationlineNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuotationlineNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QuotationlineNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
