import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutboundfailedordersComponent } from './outboundfailedorders.component';

describe('OutboundfailedordersComponent', () => {
  let component: OutboundfailedordersComponent;
  let fixture: ComponentFixture<OutboundfailedordersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutboundfailedordersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutboundfailedordersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
