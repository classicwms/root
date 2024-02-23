import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingmodeComponent } from './billingmode.component';

describe('BillingmodeComponent', () => {
  let component: BillingmodeComponent;
  let fixture: ComponentFixture<BillingmodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BillingmodeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BillingmodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
