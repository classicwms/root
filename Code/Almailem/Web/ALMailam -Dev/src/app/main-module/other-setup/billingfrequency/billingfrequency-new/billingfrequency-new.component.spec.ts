import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingfrequencyNewComponent } from './billingfrequency-new.component';

describe('BillingfrequencyNewComponent', () => {
  let component: BillingfrequencyNewComponent;
  let fixture: ComponentFixture<BillingfrequencyNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BillingfrequencyNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BillingfrequencyNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
