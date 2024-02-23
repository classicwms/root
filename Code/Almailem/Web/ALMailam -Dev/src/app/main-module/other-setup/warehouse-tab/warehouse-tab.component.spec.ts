import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehouseTabComponent } from './warehouse-tab.component';

describe('WarehouseTabComponent', () => {
  let component: WarehouseTabComponent;
  let fixture: ComponentFixture<WarehouseTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WarehouseTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehouseTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
