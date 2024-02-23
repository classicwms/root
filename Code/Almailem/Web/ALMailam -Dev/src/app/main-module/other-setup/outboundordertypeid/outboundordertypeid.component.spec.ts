import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutboundordertypeidComponent } from './outboundordertypeid.component';

describe('OutboundordertypeidComponent', () => {
  let component: OutboundordertypeidComponent;
  let fixture: ComponentFixture<OutboundordertypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutboundordertypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutboundordertypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
