import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkcenterNewComponent } from './workcenter-new.component';

describe('WorkcenterNewComponent', () => {
  let component: WorkcenterNewComponent;
  let fixture: ComponentFixture<WorkcenterNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkcenterNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkcenterNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
