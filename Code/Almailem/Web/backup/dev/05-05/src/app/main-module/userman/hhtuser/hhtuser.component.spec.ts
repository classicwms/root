import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HhtuserComponent } from './hhtuser.component';

describe('HhtuserComponent', () => {
  let component: HhtuserComponent;
  let fixture: ComponentFixture<HhtuserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HhtuserComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HhtuserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
