import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingDetailsComponent } from './picking-details.component';

describe('PickingDetailsComponent', () => {
  let component: PickingDetailsComponent;
  let fixture: ComponentFixture<PickingDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
