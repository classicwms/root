import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AltUomComponent } from './alt-uom.component';

describe('AltUomComponent', () => {
  let component: AltUomComponent;
  let fixture: ComponentFixture<AltUomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AltUomComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AltUomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
