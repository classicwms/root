import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockmovenmentListComponent } from './stockmovenment-list.component';

describe('StockmovenmentListComponent', () => {
  let component: StockmovenmentListComponent;
  let fixture: ComponentFixture<StockmovenmentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StockmovenmentListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StockmovenmentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
