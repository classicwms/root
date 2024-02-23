import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicHeaderComponent } from './periodic-header.component';

describe('PeriodicHeaderComponent', () => {
  let component: PeriodicHeaderComponent;
  let fixture: ComponentFixture<PeriodicHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PeriodicHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodicHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
