import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BomTableComponent } from './bom-table.component';

describe('BomTableComponent', () => {
  let component: BomTableComponent;
  let fixture: ComponentFixture<BomTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BomTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BomTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
