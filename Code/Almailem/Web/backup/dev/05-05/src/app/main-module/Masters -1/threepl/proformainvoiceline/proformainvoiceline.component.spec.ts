import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProformainvoicelineComponent } from './proformainvoiceline.component';

describe('ProformainvoicelineComponent', () => {
  let component: ProformainvoicelineComponent;
  let fixture: ComponentFixture<ProformainvoicelineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProformainvoicelineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProformainvoicelineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
