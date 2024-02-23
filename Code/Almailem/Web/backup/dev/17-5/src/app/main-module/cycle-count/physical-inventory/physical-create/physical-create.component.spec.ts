import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhysicalCreateComponent } from './physical-create.component';

describe('PhysicalCreateComponent', () => {
  let component: PhysicalCreateComponent;
  let fixture: ComponentFixture<PhysicalCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PhysicalCreateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PhysicalCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
