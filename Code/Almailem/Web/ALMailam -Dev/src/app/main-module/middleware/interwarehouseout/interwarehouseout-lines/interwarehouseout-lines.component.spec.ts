import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterwarehouseoutLinesComponent } from './interwarehouseout-lines.component';

describe('InterwarehouseoutLinesComponent', () => {
  let component: InterwarehouseoutLinesComponent;
  let fixture: ComponentFixture<InterwarehouseoutLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InterwarehouseoutLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InterwarehouseoutLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
