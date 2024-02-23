import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingReportUsersComponent } from './picking-report-users.component';

describe('PickingReportUsersComponent', () => {
  let component: PickingReportUsersComponent;
  let fixture: ComponentFixture<PickingReportUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingReportUsersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingReportUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
