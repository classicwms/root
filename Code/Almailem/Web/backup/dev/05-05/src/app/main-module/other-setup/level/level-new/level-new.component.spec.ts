import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LevelNewComponent } from './level-new.component';

describe('LevelNewComponent', () => {
  let component: LevelNewComponent;
  let fixture: ComponentFixture<LevelNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LevelNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LevelNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
