import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotationlineComponent } from './quotationline.component';

describe('QuotationlineComponent', () => {
  let component: QuotationlineComponent;
  let fixture: ComponentFixture<QuotationlineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuotationlineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QuotationlineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
