import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymenttermidNewComponent } from './paymenttermid-new.component';

describe('PaymenttermidNewComponent', () => {
  let component: PaymenttermidNewComponent;
  let fixture: ComponentFixture<PaymenttermidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymenttermidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymenttermidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
