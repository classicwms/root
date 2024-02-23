import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundorderstatusidComponent } from './inboundorderstatusid.component';

describe('InboundorderstatusidComponent', () => {
  let component: InboundorderstatusidComponent;
  let fixture: ComponentFixture<InboundorderstatusidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundorderstatusidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundorderstatusidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
