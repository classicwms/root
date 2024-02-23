import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehouseNewComponent } from './warehouse-new.component';

describe('WarehouseNewComponent', () => {
  let component: WarehouseNewComponent;
  let fixture: ComponentFixture<WarehouseNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WarehouseNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehouseNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
