import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasicData2Component } from './basic-data2.component';

describe('BasicData2Component', () => {
  let component: BasicData2Component;
  let fixture: ComponentFixture<BasicData2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BasicData2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BasicData2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
