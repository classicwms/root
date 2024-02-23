import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutboundsucessordersNewComponent } from './outboundsucessorders-new.component';

describe('OutboundsucessordersNewComponent', () => {
  let component: OutboundsucessordersNewComponent;
  let fixture: ComponentFixture<OutboundsucessordersNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutboundsucessordersNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutboundsucessordersNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
