import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Packdetails1Component } from './packdetails1.component';

describe('Packdetails1Component', () => {
  let component: Packdetails1Component;
  let fixture: ComponentFixture<Packdetails1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Packdetails1Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Packdetails1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
