import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HandlingequipmentidComponent } from './handlingequipmentid.component';

describe('HandlingequipmentidComponent', () => {
  let component: HandlingequipmentidComponent;
  let fixture: ComponentFixture<HandlingequipmentidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HandlingequipmentidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HandlingequipmentidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
