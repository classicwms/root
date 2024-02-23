import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DooridNewComponent } from './doorid-new.component';

describe('DooridNewComponent', () => {
  let component: DooridNewComponent;
  let fixture: ComponentFixture<DooridNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DooridNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DooridNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
