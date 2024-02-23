import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DockidNewComponent } from './dockid-new.component';

describe('DockidNewComponent', () => {
  let component: DockidNewComponent;
  let fixture: ComponentFixture<DockidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DockidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DockidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
