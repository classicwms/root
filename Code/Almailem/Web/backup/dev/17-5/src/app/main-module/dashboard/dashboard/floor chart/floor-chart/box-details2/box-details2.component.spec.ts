import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoxDetails2Component } from './box-details2.component';

describe('BoxDetails2Component', () => {
  let component: BoxDetails2Component;
  let fixture: ComponentFixture<BoxDetails2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BoxDetails2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BoxDetails2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
