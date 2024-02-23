import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundordertypeidNewComponent } from './inboundordertypeid-new.component';

describe('InboundordertypeidNewComponent', () => {
  let component: InboundordertypeidNewComponent;
  let fixture: ComponentFixture<InboundordertypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundordertypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundordertypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
