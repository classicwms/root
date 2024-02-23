import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerpetualVarianceConfirmComponent } from './perpetual-variance-confirm.component';

describe('PerpetualVarianceConfirmComponent', () => {
  let component: PerpetualVarianceConfirmComponent;
  let fixture: ComponentFixture<PerpetualVarianceConfirmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerpetualVarianceConfirmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PerpetualVarianceConfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
