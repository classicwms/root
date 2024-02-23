import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterwarehouseoutComponent } from './interwarehouseout.component';

describe('InterwarehouseoutComponent', () => {
  let component: InterwarehouseoutComponent;
  let fixture: ComponentFixture<InterwarehouseoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InterwarehouseoutComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InterwarehouseoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
