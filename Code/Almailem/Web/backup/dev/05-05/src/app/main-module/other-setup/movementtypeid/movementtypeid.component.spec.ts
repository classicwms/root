import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovementtypeidComponent } from './movementtypeid.component';

describe('MovementtypeidComponent', () => {
  let component: MovementtypeidComponent;
  let fixture: ComponentFixture<MovementtypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MovementtypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MovementtypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
