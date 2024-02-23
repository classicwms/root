import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionhistrylinesComponent } from './transactionhistrylines.component';

describe('TransactionhistrylinesComponent', () => {
  let component: TransactionhistrylinesComponent;
  let fixture: ComponentFixture<TransactionhistrylinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionhistrylinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionhistrylinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
