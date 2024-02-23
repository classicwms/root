import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesInvoiceMidllewareComponent } from './sales-invoice-midlleware.component';

describe('SalesInvoiceMidllewareComponent', () => {
  let component: SalesInvoiceMidllewareComponent;
  let fixture: ComponentFixture<SalesInvoiceMidllewareComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalesInvoiceMidllewareComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesInvoiceMidllewareComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
