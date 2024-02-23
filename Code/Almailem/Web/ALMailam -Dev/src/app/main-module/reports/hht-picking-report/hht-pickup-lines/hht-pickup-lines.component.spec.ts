import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HhtPickupLinesComponent } from './hht-pickup-lines.component';

describe('HhtPickupLinesComponent', () => {
  let component: HhtPickupLinesComponent;
  let fixture: ComponentFixture<HhtPickupLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HhtPickupLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HhtPickupLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
