import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingMainComponent } from './picking-main.component';

describe('PickingMainComponent', () => {
  let component: PickingMainComponent;
  let fixture: ComponentFixture<PickingMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
