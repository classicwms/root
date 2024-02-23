import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehouseMainComponent } from './warehouse-main.component';

describe('WarehouseMainComponent', () => {
  let component: WarehouseMainComponent;
  let fixture: ComponentFixture<WarehouseMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WarehouseMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehouseMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
