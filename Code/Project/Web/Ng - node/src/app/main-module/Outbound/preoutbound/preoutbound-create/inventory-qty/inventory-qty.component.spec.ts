import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryQtyComponent } from './inventory-qty.component';

describe('InventoryQtyComponent', () => {
  let component: InventoryQtyComponent;
  let fixture: ComponentFixture<InventoryQtyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InventoryQtyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InventoryQtyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
