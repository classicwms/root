import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PalletizationLevelIdComponent } from './palletization-level-id.component';

describe('PalletizationLevelIdComponent', () => {
  let component: PalletizationLevelIdComponent;
  let fixture: ComponentFixture<PalletizationLevelIdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PalletizationLevelIdComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PalletizationLevelIdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
