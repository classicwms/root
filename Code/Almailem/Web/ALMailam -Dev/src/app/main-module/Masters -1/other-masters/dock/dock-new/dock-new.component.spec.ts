import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DockNewComponent } from './dock-new.component';

describe('DockNewComponent', () => {
  let component: DockNewComponent;
  let fixture: ComponentFixture<DockNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DockNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DockNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
