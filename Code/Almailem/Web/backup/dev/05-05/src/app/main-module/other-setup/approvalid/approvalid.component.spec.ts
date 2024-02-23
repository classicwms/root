import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprovalidComponent } from './approvalid.component';

describe('ApprovalidComponent', () => {
  let component: ApprovalidComponent;
  let fixture: ComponentFixture<ApprovalidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApprovalidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApprovalidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
