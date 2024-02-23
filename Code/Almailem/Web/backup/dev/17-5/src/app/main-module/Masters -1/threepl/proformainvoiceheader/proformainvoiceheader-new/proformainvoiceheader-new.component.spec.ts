import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProformainvoiceheaderNewComponent } from './proformainvoiceheader-new.component';

describe('ProformainvoiceheaderNewComponent', () => {
  let component: ProformainvoiceheaderNewComponent;
  let fixture: ComponentFixture<ProformainvoiceheaderNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProformainvoiceheaderNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProformainvoiceheaderNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
