import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhysicalTabComponent } from './physical-tab.component';

describe('PhysicalTabComponent', () => {
  let component: PhysicalTabComponent;
  let fixture: ComponentFixture<PhysicalTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PhysicalTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PhysicalTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
