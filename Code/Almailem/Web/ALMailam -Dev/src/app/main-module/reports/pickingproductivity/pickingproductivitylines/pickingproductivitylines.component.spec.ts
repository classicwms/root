import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingproductivitylinesComponent } from './pickingproductivitylines.component';

describe('PickingproductivitylinesComponent', () => {
  let component: PickingproductivitylinesComponent;
  let fixture: ComponentFixture<PickingproductivitylinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingproductivitylinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingproductivitylinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
