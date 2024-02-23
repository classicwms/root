import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundTabbarComponent } from './inbound-tabbar.component';

describe('InboundTabbarComponent', () => {
  let component: InboundTabbarComponent;
  let fixture: ComponentFixture<InboundTabbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundTabbarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundTabbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
