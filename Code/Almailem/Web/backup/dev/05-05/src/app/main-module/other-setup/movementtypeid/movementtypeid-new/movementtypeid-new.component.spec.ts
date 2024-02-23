import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovementtypeidNewComponent } from './movementtypeid-new.component';

describe('MovementtypeidNewComponent', () => {
  let component: MovementtypeidNewComponent;
  let fixture: ComponentFixture<MovementtypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MovementtypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MovementtypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
