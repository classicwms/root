import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignHEComponent } from './assign-he.component';

describe('AssignHEComponent', () => {
  let component: AssignHEComponent;
  let fixture: ComponentFixture<AssignHEComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignHEComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignHEComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
