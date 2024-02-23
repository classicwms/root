import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignInvoiceComponent } from './assign-invoice.component';

describe('AssignInvoiceComponent', () => {
  let component: AssignInvoiceComponent;
  let fixture: ComponentFixture<AssignInvoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignInvoiceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignInvoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
