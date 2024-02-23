import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FastSlowListComponent } from './fast-slow-list.component';

describe('FastSlowListComponent', () => {
  let component: FastSlowListComponent;
  let fixture: ComponentFixture<FastSlowListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FastSlowListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FastSlowListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
