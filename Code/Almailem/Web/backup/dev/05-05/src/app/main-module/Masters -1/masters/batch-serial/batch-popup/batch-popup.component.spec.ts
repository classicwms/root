import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchPopupComponent } from './batch-popup.component';

describe('BatchPopupComponent', () => {
  let component: BatchPopupComponent;
  let fixture: ComponentFixture<BatchPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BatchPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BatchPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
