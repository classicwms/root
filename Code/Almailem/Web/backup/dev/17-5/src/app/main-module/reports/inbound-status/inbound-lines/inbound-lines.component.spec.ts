import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundLinesComponent } from './inbound-lines.component';

describe('InboundLinesComponent', () => {
  let component: InboundLinesComponent;
  let fixture: ComponentFixture<InboundLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
