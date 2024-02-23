import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ControlprocessidComponent } from './controlprocessid.component';

describe('ControlprocessidComponent', () => {
  let component: ControlprocessidComponent;
  let fixture: ComponentFixture<ControlprocessidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ControlprocessidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ControlprocessidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
