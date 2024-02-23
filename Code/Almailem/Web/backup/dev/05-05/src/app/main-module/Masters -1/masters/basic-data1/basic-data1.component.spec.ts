import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasicData1Component } from './basic-data1.component';

describe('BasicData1Component', () => {
  let component: BasicData1Component;
  let fixture: ComponentFixture<BasicData1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BasicData1Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BasicData1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
