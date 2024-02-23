import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveredCompletedComponent } from './delivered-completed.component';

describe('DeliveredCompletedComponent', () => {
  let component: DeliveredCompletedComponent;
  let fixture: ComponentFixture<DeliveredCompletedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliveredCompletedComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliveredCompletedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
