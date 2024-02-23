import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingformatComponent } from './billingformat.component';

describe('BillingformatComponent', () => {
  let component: BillingformatComponent;
  let fixture: ComponentFixture<BillingformatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BillingformatComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BillingformatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
