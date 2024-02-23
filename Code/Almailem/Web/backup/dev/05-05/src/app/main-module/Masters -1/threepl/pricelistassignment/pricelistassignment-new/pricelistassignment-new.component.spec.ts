import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PricelistassignmentNewComponent } from './pricelistassignment-new.component';

describe('PricelistassignmentNewComponent', () => {
  let component: PricelistassignmentNewComponent;
  let fixture: ComponentFixture<PricelistassignmentNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PricelistassignmentNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PricelistassignmentNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
