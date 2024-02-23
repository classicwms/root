import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentsummaryListComponent } from './shipmentsummary-list.component';

describe('ShipmentsummaryListComponent', () => {
  let component: ShipmentsummaryListComponent;
  let fixture: ComponentFixture<ShipmentsummaryListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShipmentsummaryListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShipmentsummaryListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
