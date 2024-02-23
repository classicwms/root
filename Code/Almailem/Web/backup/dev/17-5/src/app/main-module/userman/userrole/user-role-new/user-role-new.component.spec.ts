import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRoleNewComponent } from './user-role-new.component';

describe('UserRoleNewComponent', () => {
  let component: UserRoleNewComponent;
  let fixture: ComponentFixture<UserRoleNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserRoleNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRoleNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
