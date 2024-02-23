import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumberrangestoragebinNewComponent } from './numberrangestoragebin-new.component';

describe('NumberrangestoragebinNewComponent', () => {
  let component: NumberrangestoragebinNewComponent;
  let fixture: ComponentFixture<NumberrangestoragebinNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumberrangestoragebinNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumberrangestoragebinNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
