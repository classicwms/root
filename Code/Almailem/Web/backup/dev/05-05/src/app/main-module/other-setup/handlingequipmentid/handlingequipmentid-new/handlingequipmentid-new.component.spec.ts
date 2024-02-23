import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HandlingequipmentidNewComponent } from './handlingequipmentid-new.component';

describe('HandlingequipmentidNewComponent', () => {
  let component: HandlingequipmentidNewComponent;
  let fixture: ComponentFixture<HandlingequipmentidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HandlingequipmentidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HandlingequipmentidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
