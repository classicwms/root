import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PalletaddlinesComponent } from './palletaddlines.component';

describe('PalletaddlinesComponent', () => {
  let component: PalletaddlinesComponent;
  let fixture: ComponentFixture<PalletaddlinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PalletaddlinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PalletaddlinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
