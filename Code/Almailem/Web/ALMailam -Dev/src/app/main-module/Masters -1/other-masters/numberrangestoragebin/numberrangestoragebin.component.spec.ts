import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumberrangestoragebinComponent } from './numberrangestoragebin.component';

describe('NumberrangestoragebinComponent', () => {
  let component: NumberrangestoragebinComponent;
  let fixture: ComponentFixture<NumberrangestoragebinComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumberrangestoragebinComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumberrangestoragebinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
