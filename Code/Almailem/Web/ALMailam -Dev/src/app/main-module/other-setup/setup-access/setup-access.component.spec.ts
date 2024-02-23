import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupAccessComponent } from './setup-access.component';

describe('SetupAccessComponent', () => {
  let component: SetupAccessComponent;
  let fixture: ComponentFixture<SetupAccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SetupAccessComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SetupAccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
