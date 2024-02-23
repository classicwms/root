import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutboundsucessordersComponent } from './outboundsucessorders.component';

describe('OutboundsucessordersComponent', () => {
  let component: OutboundsucessordersComponent;
  let fixture: ComponentFixture<OutboundsucessordersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutboundsucessordersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutboundsucessordersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
