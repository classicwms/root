import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkuSkuComponent } from './sku-sku.component';

describe('SkuSkuComponent', () => {
  let component: SkuSkuComponent;
  let fixture: ComponentFixture<SkuSkuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SkuSkuComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SkuSkuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
