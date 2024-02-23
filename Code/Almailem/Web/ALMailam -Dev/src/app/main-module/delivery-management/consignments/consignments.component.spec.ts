import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsignmentsComponent } from './consignments.component';

describe('ConsignmentsComponent', () => {
  let component: ConsignmentsComponent;
  let fixture: ComponentFixture<ConsignmentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConsignmentsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsignmentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
