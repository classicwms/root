import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickupCreationComponent } from './pickup-creation.component';

describe('PickupCreationComponent', () => {
  let component: PickupCreationComponent;
  let fixture: ComponentFixture<PickupCreationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickupCreationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickupCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
