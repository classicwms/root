import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprovalprocessidNewComponent } from './approvalprocessid-new.component';

describe('ApprovalprocessidNewComponent', () => {
  let component: ApprovalprocessidNewComponent;
  let fixture: ComponentFixture<ApprovalprocessidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApprovalprocessidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApprovalprocessidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
