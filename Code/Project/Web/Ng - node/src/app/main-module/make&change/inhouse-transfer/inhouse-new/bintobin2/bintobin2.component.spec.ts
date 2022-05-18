import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Bintobin2Component } from './bintobin2.component';

describe('Bintobin2Component', () => {
  let component: Bintobin2Component;
  let fixture: ComponentFixture<Bintobin2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Bintobin2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Bintobin2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
