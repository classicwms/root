import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateOutboundLineComponent } from './update-outbound-line.component';

describe('UpdateOutboundLineComponent', () => {
  let component: UpdateOutboundLineComponent;
  let fixture: ComponentFixture<UpdateOutboundLineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateOutboundLineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateOutboundLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
