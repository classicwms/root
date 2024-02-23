import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboundCreateComponent } from './inbound-create.component';

describe('InboundCreateComponent', () => {
  let component: InboundCreateComponent;
  let fixture: ComponentFixture<InboundCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboundCreateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboundCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
