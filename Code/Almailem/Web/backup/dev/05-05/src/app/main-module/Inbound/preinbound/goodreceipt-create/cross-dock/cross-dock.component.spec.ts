import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrossDockComponent } from './cross-dock.component';

describe('CrossDockComponent', () => {
  let component: CrossDockComponent;
  let fixture: ComponentFixture<CrossDockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CrossDockComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CrossDockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
