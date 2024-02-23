import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantidComponent } from './variantid.component';

describe('VariantidComponent', () => {
  let component: VariantidComponent;
  let fixture: ComponentFixture<VariantidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VariantidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
