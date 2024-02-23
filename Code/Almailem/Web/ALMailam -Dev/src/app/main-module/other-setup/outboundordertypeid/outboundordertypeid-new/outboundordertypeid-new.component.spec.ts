import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutboundordertypeidNewComponent } from './outboundordertypeid-new.component';

describe('OutboundordertypeidNewComponent', () => {
  let component: OutboundordertypeidNewComponent;
  let fixture: ComponentFixture<OutboundordertypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutboundordertypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutboundordertypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
