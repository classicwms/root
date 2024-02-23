import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingConfirmComponent } from './picking-confirm.component';

describe('PickingConfirmComponent', () => {
  let component: PickingConfirmComponent;
  let fixture: ComponentFixture<PickingConfirmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingConfirmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingConfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
