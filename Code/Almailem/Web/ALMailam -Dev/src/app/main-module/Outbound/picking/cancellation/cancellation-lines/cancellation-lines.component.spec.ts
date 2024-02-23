import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancellationLinesComponent } from './cancellation-lines.component';

describe('CancellationLinesComponent', () => {
  let component: CancellationLinesComponent;
  let fixture: ComponentFixture<CancellationLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CancellationLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CancellationLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
