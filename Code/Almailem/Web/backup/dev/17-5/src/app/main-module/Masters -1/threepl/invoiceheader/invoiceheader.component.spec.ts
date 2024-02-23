import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceheaderComponent } from './invoiceheader.component';

describe('InvoiceheaderComponent', () => {
  let component: InvoiceheaderComponent;
  let fixture: ComponentFixture<InvoiceheaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InvoiceheaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvoiceheaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
