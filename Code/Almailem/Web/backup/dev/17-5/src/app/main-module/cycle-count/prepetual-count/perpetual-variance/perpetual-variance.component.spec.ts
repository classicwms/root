import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerpetualVarianceComponent } from './perpetual-variance.component';

describe('PerpetualVarianceComponent', () => {
  let component: PerpetualVarianceComponent;
  let fixture: ComponentFixture<PerpetualVarianceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerpetualVarianceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PerpetualVarianceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
