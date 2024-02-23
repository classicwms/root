import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundorderstatusNewComponent } from './inboundorderstatus-new.component';

describe('InboundorderstatusNewComponent', () => {
  let component: InboundorderstatusNewComponent;
  let fixture: ComponentFixture<InboundorderstatusNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundorderstatusNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundorderstatusNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
