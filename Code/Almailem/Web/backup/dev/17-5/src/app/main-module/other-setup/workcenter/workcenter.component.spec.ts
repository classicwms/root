import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkcenterComponent } from './workcenter.component';

describe('WorkcenterComponent', () => {
  let component: WorkcenterComponent;
  let fixture: ComponentFixture<WorkcenterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkcenterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkcenterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
