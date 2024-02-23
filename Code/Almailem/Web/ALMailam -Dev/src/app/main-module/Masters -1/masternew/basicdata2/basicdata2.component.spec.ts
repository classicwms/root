import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Basicdata2Component } from './basicdata2.component';

describe('Basicdata2Component', () => {
  let component: Basicdata2Component;
  let fixture: ComponentFixture<Basicdata2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Basicdata2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Basicdata2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
