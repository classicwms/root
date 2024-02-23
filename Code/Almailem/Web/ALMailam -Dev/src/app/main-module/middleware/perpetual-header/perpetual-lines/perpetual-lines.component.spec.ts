import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerpetualLinesComponent } from './perpetual-lines.component';

describe('PerpetualLinesComponent', () => {
  let component: PerpetualLinesComponent;
  let fixture: ComponentFixture<PerpetualLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerpetualLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PerpetualLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
