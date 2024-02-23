import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusmessagesidNewComponent } from './statusmessagesid-new.component';

describe('StatusmessagesidNewComponent', () => {
  let component: StatusmessagesidNewComponent;
  let fixture: ComponentFixture<StatusmessagesidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StatusmessagesidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusmessagesidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
