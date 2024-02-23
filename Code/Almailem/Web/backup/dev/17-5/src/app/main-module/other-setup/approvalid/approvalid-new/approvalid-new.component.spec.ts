import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprovalidNewComponent } from './approvalid-new.component';

describe('ApprovalidNewComponent', () => {
  let component: ApprovalidNewComponent;
  let fixture: ComponentFixture<ApprovalidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApprovalidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApprovalidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
