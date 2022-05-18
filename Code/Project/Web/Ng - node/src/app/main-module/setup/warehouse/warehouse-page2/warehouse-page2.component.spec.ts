import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehousePage2Component } from './warehouse-page2.component';

describe('WarehousePage2Component', () => {
  let component: WarehousePage2Component;
  let fixture: ComponentFixture<WarehousePage2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WarehousePage2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehousePage2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
