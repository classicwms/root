import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkuPopupComponent } from './sku-popup.component';

describe('SkuPopupComponent', () => {
  let component: SkuPopupComponent;
  let fixture: ComponentFixture<SkuPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SkuPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SkuPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
