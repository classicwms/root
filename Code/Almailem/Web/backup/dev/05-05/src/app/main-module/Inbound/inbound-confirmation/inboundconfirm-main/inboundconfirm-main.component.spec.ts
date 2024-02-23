import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundconfirmMainComponent } from './inboundconfirm-main.component';

describe('InboundconfirmMainComponent', () => {
  let component: InboundconfirmMainComponent;
  let fixture: ComponentFixture<InboundconfirmMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundconfirmMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundconfirmMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
