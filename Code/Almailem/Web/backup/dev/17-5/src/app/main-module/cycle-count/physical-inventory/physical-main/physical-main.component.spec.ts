import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhysicalMainComponent } from './physical-main.component';

describe('PhysicalMainComponent', () => {
  let component: PhysicalMainComponent;
  let fixture: ComponentFixture<PhysicalMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PhysicalMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PhysicalMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
