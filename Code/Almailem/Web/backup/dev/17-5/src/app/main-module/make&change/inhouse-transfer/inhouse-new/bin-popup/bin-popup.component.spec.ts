import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinPopupComponent } from './bin-popup.component';

describe('BinPopupComponent', () => {
  let component: BinPopupComponent;
  let fixture: ComponentFixture<BinPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
