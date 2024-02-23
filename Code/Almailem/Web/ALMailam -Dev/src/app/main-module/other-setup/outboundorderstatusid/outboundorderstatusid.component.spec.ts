import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutboundorderstatusidComponent } from './outboundorderstatusid.component';

describe('OutboundorderstatusidComponent', () => {
  let component: OutboundorderstatusidComponent;
  let fixture: ComponentFixture<OutboundorderstatusidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutboundorderstatusidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutboundorderstatusidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
