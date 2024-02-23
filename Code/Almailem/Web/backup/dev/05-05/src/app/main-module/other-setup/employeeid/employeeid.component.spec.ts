import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeidComponent } from './employeeid.component';

describe('EmployeeidComponent', () => {
  let component: EmployeeidComponent;
  let fixture: ComponentFixture<EmployeeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmployeeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmployeeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
