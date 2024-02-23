import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprovalprocessidComponent } from './approvalprocessid.component';

describe('ApprovalprocessidComponent', () => {
  let component: ApprovalprocessidComponent;
  let fixture: ComponentFixture<ApprovalprocessidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApprovalprocessidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApprovalprocessidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
