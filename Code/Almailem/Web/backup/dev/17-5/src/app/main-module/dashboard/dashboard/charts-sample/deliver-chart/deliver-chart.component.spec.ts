import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliverChartComponent } from './deliver-chart.component';

describe('DeliverChartComponent', () => {
  let component: DeliverChartComponent;
  let fixture: ComponentFixture<DeliverChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliverChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliverChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
