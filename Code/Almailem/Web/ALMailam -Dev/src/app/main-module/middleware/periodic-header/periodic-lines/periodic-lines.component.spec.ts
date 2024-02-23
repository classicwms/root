import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicLinesComponent } from './periodic-lines.component';

describe('PeriodicLinesComponent', () => {
  let component: PeriodicLinesComponent;
  let fixture: ComponentFixture<PeriodicLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PeriodicLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodicLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
