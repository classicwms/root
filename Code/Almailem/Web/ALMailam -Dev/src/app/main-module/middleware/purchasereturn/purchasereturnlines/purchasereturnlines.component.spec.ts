import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PurchasereturnlinesComponent } from './purchasereturnlines.component';

describe('PurchasereturnlinesComponent', () => {
  let component: PurchasereturnlinesComponent;
  let fixture: ComponentFixture<PurchasereturnlinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PurchasereturnlinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurchasereturnlinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
