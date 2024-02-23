import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarcodeNewComponent } from './barcode-new.component';

describe('BarcodeNewComponent', () => {
  let component: BarcodeNewComponent;
  let fixture: ComponentFixture<BarcodeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BarcodeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BarcodeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
