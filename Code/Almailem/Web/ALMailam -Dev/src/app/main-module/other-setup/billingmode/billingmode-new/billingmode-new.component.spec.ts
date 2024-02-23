import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingmodeNewComponent } from './billingmode-new.component';

describe('BillingmodeNewComponent', () => {
  let component: BillingmodeNewComponent;
  let fixture: ComponentFixture<BillingmodeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BillingmodeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BillingmodeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
