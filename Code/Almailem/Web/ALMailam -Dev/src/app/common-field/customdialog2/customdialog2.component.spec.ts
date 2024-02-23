import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Customdialog2Component } from './customdialog2.component';

describe('Customdialog2Component', () => {
  let component: Customdialog2Component;
  let fixture: ComponentFixture<Customdialog2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Customdialog2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Customdialog2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
