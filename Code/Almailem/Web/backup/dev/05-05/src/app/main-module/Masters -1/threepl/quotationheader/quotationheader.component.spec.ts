import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotationheaderComponent } from './quotationheader.component';

describe('QuotationheaderComponent', () => {
  let component: QuotationheaderComponent;
  let fixture: ComponentFixture<QuotationheaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuotationheaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QuotationheaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
