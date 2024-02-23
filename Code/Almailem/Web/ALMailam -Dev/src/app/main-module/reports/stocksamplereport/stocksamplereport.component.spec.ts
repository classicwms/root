import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StocksamplereportComponent } from './stocksamplereport.component';

describe('StocksamplereportComponent', () => {
  let component: StocksamplereportComponent;
  let fixture: ComponentFixture<StocksamplereportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StocksamplereportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StocksamplereportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
