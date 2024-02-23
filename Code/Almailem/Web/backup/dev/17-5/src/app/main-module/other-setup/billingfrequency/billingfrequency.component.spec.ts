import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingfrequencyComponent } from './billingfrequency.component';

describe('BillingfrequencyComponent', () => {
  let component: BillingfrequencyComponent;
  let fixture: ComponentFixture<BillingfrequencyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BillingfrequencyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BillingfrequencyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
