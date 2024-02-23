import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantAnnualComponent } from './variant-annual.component';

describe('VariantAnnualComponent', () => {
  let component: VariantAnnualComponent;
  let fixture: ComponentFixture<VariantAnnualComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VariantAnnualComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantAnnualComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
