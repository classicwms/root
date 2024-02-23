import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterwarehouseinLinesComponent } from './interwarehousein-lines.component';

describe('InterwarehouseinLinesComponent', () => {
  let component: InterwarehouseinLinesComponent;
  let fixture: ComponentFixture<InterwarehouseinLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InterwarehouseinLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InterwarehouseinLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
