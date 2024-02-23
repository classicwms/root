import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantEditComponent } from './variant-edit.component';

describe('VariantEditComponent', () => {
  let component: VariantEditComponent;
  let fixture: ComponentFixture<VariantEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VariantEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
