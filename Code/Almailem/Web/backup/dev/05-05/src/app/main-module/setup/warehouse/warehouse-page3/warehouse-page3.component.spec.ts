import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehousePage3Component } from './warehouse-page3.component';

describe('WarehousePage3Component', () => {
  let component: WarehousePage3Component;
  let fixture: ComponentFixture<WarehousePage3Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WarehousePage3Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehousePage3Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
