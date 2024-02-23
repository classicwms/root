import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarcodesubtypeidNewComponent } from './barcodesubtypeid-new.component';

describe('BarcodesubtypeidNewComponent', () => {
  let component: BarcodesubtypeidNewComponent;
  let fixture: ComponentFixture<BarcodesubtypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BarcodesubtypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BarcodesubtypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
