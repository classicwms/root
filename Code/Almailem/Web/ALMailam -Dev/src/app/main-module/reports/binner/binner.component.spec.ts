import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinnerComponent } from './binner.component';

describe('BinnerComponent', () => {
  let component: BinnerComponent;
  let fixture: ComponentFixture<BinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinnerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
