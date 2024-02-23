import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockMovenmentComponent } from './stock-movenment.component';

describe('StockMovenmentComponent', () => {
  let component: StockMovenmentComponent;
  let fixture: ComponentFixture<StockMovenmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StockMovenmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StockMovenmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
