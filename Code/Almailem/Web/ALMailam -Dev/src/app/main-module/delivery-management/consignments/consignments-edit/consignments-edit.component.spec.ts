import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsignmentsEditComponent } from './consignments-edit.component';

describe('ConsignmentsEditComponent', () => {
  let component: ConsignmentsEditComponent;
  let fixture: ComponentFixture<ConsignmentsEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConsignmentsEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsignmentsEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
