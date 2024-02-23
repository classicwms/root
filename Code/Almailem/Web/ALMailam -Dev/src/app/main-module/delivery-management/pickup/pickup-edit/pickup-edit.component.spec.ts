import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickupEditComponent } from './pickup-edit.component';

describe('PickupEditComponent', () => {
  let component: PickupEditComponent;
  let fixture: ComponentFixture<PickupEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickupEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickupEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
