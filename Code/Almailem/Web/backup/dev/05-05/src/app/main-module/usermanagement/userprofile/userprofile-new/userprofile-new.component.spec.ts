import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserprofileNewComponent } from './userprofile-new.component';

describe('UserprofileNewComponent', () => {
  let component: UserprofileNewComponent;
  let fixture: ComponentFixture<UserprofileNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserprofileNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserprofileNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
