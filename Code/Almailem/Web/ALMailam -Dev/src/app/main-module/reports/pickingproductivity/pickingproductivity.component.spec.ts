import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingproductivityComponent } from './pickingproductivity.component';

describe('PickingproductivityComponent', () => {
  let component: PickingproductivityComponent;
  let fixture: ComponentFixture<PickingproductivityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingproductivityComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingproductivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
