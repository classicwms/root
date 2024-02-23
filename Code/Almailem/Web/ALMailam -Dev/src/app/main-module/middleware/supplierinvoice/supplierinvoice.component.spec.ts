import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierinvoiceComponent } from './supplierinvoice.component';

describe('SupplierinvoiceComponent', () => {
  let component: SupplierinvoiceComponent;
  let fixture: ComponentFixture<SupplierinvoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupplierinvoiceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupplierinvoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
