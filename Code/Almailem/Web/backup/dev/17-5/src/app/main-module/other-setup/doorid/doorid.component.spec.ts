import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DooridComponent } from './doorid.component';

describe('DooridComponent', () => {
  let component: DooridComponent;
  let fixture: ComponentFixture<DooridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DooridComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DooridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
