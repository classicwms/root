import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PricelistassignmentComponent } from './pricelistassignment.component';

describe('PricelistassignmentComponent', () => {
  let component: PricelistassignmentComponent;
  let fixture: ComponentFixture<PricelistassignmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PricelistassignmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PricelistassignmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
