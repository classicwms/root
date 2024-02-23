import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsignmentsOpenComponent } from './consignments-open.component';

describe('ConsignmentsOpenComponent', () => {
  let component: ConsignmentsOpenComponent;
  let fixture: ComponentFixture<ConsignmentsOpenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConsignmentsOpenComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsignmentsOpenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
