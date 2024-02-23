import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipemntorderLinesComponent } from './shipemntorder-lines.component';

describe('ShipemntorderLinesComponent', () => {
  let component: ShipemntorderLinesComponent;
  let fixture: ComponentFixture<ShipemntorderLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShipemntorderLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShipemntorderLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
