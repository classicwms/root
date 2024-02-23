import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumberrangeNewComponent } from './numberrange-new.component';

describe('NumberrangeNewComponent', () => {
  let component: NumberrangeNewComponent;
  let fixture: ComponentFixture<NumberrangeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumberrangeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumberrangeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
