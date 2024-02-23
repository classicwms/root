import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumberrangeComponent } from './numberrange.component';

describe('NumberrangeComponent', () => {
  let component: NumberrangeComponent;
  let fixture: ComponentFixture<NumberrangeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumberrangeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumberrangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
