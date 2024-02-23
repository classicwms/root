import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HandlingunitNewComponent } from './handlingunit-new.component';

describe('HandlingunitNewComponent', () => {
  let component: HandlingunitNewComponent;
  let fixture: ComponentFixture<HandlingunitNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HandlingunitNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HandlingunitNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
