import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymenttermidComponent } from './paymenttermid.component';

describe('PaymenttermidComponent', () => {
  let component: PaymenttermidComponent;
  let fixture: ComponentFixture<PaymenttermidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymenttermidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymenttermidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
