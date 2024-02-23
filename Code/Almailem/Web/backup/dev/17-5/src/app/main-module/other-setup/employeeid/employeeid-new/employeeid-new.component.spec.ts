import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeidNewComponent } from './employeeid-new.component';

describe('EmployeeidNewComponent', () => {
  let component: EmployeeidNewComponent;
  let fixture: ComponentFixture<EmployeeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmployeeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmployeeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
