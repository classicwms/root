import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantNewComponent } from './variant-new.component';

describe('VariantNewComponent', () => {
  let component: VariantNewComponent;
  let fixture: ComponentFixture<VariantNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VariantNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
