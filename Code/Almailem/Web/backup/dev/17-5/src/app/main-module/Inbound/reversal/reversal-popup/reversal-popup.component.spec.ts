import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReversalPopupComponent } from './reversal-popup.component';

describe('ReversalPopupComponent', () => {
  let component: ReversalPopupComponent;
  let fixture: ComponentFixture<ReversalPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReversalPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReversalPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
