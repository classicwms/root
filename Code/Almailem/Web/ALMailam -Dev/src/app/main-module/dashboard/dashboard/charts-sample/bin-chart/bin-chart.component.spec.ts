import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinChartComponent } from './bin-chart.component';

describe('BinChartComponent', () => {
  let component: BinChartComponent;
  let fixture: ComponentFixture<BinChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
