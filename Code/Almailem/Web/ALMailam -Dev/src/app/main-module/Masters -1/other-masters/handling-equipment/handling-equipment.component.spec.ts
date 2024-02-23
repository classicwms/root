import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HandlingEquipmentComponent } from './handling-equipment.component';

describe('HandlingEquipmentComponent', () => {
  let component: HandlingEquipmentComponent;
  let fixture: ComponentFixture<HandlingEquipmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HandlingEquipmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HandlingEquipmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
