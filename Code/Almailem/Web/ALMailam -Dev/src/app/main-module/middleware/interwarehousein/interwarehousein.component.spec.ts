import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterwarehouseinComponent } from './interwarehousein.component';

describe('InterwarehouseinComponent', () => {
  let component: InterwarehouseinComponent;
  let fixture: ComponentFixture<InterwarehouseinComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InterwarehouseinComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InterwarehouseinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
