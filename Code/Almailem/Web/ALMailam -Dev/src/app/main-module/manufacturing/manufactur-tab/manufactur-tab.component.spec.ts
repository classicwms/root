import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManufacturTabComponent } from './manufactur-tab.component';

describe('ManufacturTabComponent', () => {
  let component: ManufacturTabComponent;
  let fixture: ComponentFixture<ManufacturTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManufacturTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManufacturTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
