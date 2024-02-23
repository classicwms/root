import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarcodesubtypeidComponent } from './barcodesubtypeid.component';

describe('BarcodesubtypeidComponent', () => {
  let component: BarcodesubtypeidComponent;
  let fixture: ComponentFixture<BarcodesubtypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BarcodesubtypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BarcodesubtypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
