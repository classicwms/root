import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupTabComponent } from './setup-tab.component';

describe('SetupTabComponent', () => {
  let component: SetupTabComponent;
  let fixture: ComponentFixture<SetupTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SetupTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SetupTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
