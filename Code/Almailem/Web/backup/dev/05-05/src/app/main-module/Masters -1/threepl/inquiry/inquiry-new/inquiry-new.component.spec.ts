import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InquiryNewComponent } from './inquiry-new.component';

describe('InquiryNewComponent', () => {
  let component: InquiryNewComponent;
  let fixture: ComponentFixture<InquiryNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InquiryNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InquiryNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
