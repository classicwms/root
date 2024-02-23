import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingformatNewComponent } from './billingformat-new.component';

describe('BillingformatNewComponent', () => {
  let component: BillingformatNewComponent;
  let fixture: ComponentFixture<BillingformatNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BillingformatNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BillingformatNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
