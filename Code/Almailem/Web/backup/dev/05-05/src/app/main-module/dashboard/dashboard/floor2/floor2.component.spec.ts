import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Floor2Component } from './floor2.component';

describe('Floor2Component', () => {
  let component: Floor2Component;
  let fixture: ComponentFixture<Floor2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Floor2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Floor2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
