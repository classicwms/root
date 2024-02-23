import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumberrangeitemNewComponent } from './numberrangeitem-new.component';

describe('NumberrangeitemNewComponent', () => {
  let component: NumberrangeitemNewComponent;
  let fixture: ComponentFixture<NumberrangeitemNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumberrangeitemNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumberrangeitemNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
