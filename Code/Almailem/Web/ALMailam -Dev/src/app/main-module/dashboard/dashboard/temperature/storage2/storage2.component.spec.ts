import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Storage2Component } from './storage2.component';

describe('Storage2Component', () => {
  let component: Storage2Component;
  let fixture: ComponentFixture<Storage2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Storage2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Storage2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
