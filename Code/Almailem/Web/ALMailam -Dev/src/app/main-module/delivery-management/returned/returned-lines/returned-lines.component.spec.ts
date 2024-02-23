import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReturnedLinesComponent } from './returned-lines.component';

describe('ReturnedLinesComponent', () => {
  let component: ReturnedLinesComponent;
  let fixture: ComponentFixture<ReturnedLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReturnedLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReturnedLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
