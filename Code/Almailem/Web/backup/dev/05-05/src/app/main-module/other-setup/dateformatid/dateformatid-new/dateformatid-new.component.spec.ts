import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateformatidNewComponent } from './dateformatid-new.component';

describe('DateformatidNewComponent', () => {
  let component: DateformatidNewComponent;
  let fixture: ComponentFixture<DateformatidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DateformatidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateformatidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
