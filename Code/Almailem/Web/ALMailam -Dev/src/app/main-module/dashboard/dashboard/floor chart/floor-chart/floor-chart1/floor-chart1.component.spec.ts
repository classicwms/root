import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FloorChart1Component } from './floor-chart1.component';

describe('FloorChart1Component', () => {
  let component: FloorChart1Component;
  let fixture: ComponentFixture<FloorChart1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FloorChart1Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FloorChart1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
