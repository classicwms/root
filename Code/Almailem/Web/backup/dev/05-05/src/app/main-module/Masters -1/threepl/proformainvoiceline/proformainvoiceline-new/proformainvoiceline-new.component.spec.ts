import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProformainvoicelineNewComponent } from './proformainvoiceline-new.component';

describe('ProformainvoicelineNewComponent', () => {
  let component: ProformainvoicelineNewComponent;
  let fixture: ComponentFixture<ProformainvoicelineNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProformainvoicelineNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProformainvoicelineNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
