import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumberrangeitemComponent } from './numberrangeitem.component';

describe('NumberrangeitemComponent', () => {
  let component: NumberrangeitemComponent;
  let fixture: ComponentFixture<NumberrangeitemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumberrangeitemComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumberrangeitemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
