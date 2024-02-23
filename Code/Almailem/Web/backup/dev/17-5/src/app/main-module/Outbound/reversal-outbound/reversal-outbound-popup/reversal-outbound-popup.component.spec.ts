import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReversalOutboundPopupComponent } from './reversal-outbound-popup.component';

describe('ReversalOutboundPopupComponent', () => {
  let component: ReversalOutboundPopupComponent;
  let fixture: ComponentFixture<ReversalOutboundPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReversalOutboundPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReversalOutboundPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
