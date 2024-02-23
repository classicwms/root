import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutboundstatusTabComponent } from './outboundstatus-tab.component';

describe('OutboundstatusTabComponent', () => {
  let component: OutboundstatusTabComponent;
  let fixture: ComponentFixture<OutboundstatusTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutboundstatusTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutboundstatusTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
