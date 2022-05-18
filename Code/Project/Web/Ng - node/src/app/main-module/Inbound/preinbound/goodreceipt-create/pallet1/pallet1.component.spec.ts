import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Pallet1Component } from './pallet1.component';

describe('Pallet1Component', () => {
  let component: Pallet1Component;
  let fixture: ComponentFixture<Pallet1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Pallet1Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Pallet1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
