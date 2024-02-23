import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PalletizationLevelIdNewComponent } from './palletization-level-id-new.component';

describe('PalletizationLevelIdNewComponent', () => {
  let component: PalletizationLevelIdNewComponent;
  let fixture: ComponentFixture<PalletizationLevelIdNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PalletizationLevelIdNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PalletizationLevelIdNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
