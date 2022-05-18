import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhysicalCountingComponent } from './physical-counting.component';

describe('PhysicalCountingComponent', () => {
  let component: PhysicalCountingComponent;
  let fixture: ComponentFixture<PhysicalCountingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PhysicalCountingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PhysicalCountingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
