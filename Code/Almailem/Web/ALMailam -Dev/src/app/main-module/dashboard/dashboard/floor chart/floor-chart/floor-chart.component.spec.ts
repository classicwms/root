import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FloorChartComponent } from './floor-chart.component';

describe('FloorChartComponent', () => {
  let component: FloorChartComponent;
  let fixture: ComponentFixture<FloorChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FloorChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FloorChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
