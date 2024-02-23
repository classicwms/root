import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusidNewComponent } from './statusid-new.component';

describe('StatusidNewComponent', () => {
  let component: StatusidNewComponent;
  let fixture: ComponentFixture<StatusidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StatusidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
