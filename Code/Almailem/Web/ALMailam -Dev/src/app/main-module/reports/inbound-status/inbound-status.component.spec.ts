import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundStatusComponent } from './inbound-status.component';

describe('InboundStatusComponent', () => {
  let component: InboundStatusComponent;
  let fixture: ComponentFixture<InboundStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundStatusComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
