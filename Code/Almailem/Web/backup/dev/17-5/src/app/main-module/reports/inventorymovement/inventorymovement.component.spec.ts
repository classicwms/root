import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventorymovementComponent } from './inventorymovement.component';

describe('InventorymovementComponent', () => {
  let component: InventorymovementComponent;
  let fixture: ComponentFixture<InventorymovementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InventorymovementComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InventorymovementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
