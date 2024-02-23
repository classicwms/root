import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DockidComponent } from './dockid.component';

describe('DockidComponent', () => {
  let component: DockidComponent;
  let fixture: ComponentFixture<DockidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DockidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DockidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
