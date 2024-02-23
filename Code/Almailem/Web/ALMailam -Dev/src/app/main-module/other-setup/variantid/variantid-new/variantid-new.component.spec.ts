import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantidNewComponent } from './variantid-new.component';

describe('VariantidNewComponent', () => {
  let component: VariantidNewComponent;
  let fixture: ComponentFixture<VariantidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VariantidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
