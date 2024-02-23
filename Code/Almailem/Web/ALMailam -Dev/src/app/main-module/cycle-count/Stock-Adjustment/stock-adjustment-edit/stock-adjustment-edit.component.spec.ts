import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockAdjustmentEditComponent } from './stock-adjustment-edit.component';

describe('StockAdjustmentEditComponent', () => {
  let component: StockAdjustmentEditComponent;
  let fixture: ComponentFixture<StockAdjustmentEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StockAdjustmentEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StockAdjustmentEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
