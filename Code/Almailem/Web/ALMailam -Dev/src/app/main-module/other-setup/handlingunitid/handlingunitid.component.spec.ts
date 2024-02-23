import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HandlingunitidComponent } from './handlingunitid.component';

describe('HandlingunitidComponent', () => {
  let component: HandlingunitidComponent;
  let fixture: ComponentFixture<HandlingunitidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HandlingunitidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HandlingunitidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
