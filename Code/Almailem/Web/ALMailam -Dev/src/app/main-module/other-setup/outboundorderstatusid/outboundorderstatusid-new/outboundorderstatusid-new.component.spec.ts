import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutboundorderstatusidNewComponent } from './outboundorderstatusid-new.component';

describe('OutboundorderstatusidNewComponent', () => {
  let component: OutboundorderstatusidNewComponent;
  let fixture: ComponentFixture<OutboundorderstatusidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutboundorderstatusidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutboundorderstatusidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
