import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierinvoiceLinesComponent } from './supplierinvoice-lines.component';

describe('SupplierinvoiceLinesComponent', () => {
  let component: SupplierinvoiceLinesComponent;
  let fixture: ComponentFixture<SupplierinvoiceLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupplierinvoiceLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupplierinvoiceLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
