import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantAnalysisComponent } from './variant-analysis.component';

describe('VariantAnalysisComponent', () => {
  let component: VariantAnalysisComponent;
  let fixture: ComponentFixture<VariantAnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VariantAnalysisComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
