import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundordertypeidComponent } from './inboundordertypeid.component';

describe('InboundordertypeidComponent', () => {
  let component: InboundordertypeidComponent;
  let fixture: ComponentFixture<InboundordertypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundordertypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundordertypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
