import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicTabComponent } from './periodic-tab.component';

describe('PeriodicTabComponent', () => {
  let component: PeriodicTabComponent;
  let fixture: ComponentFixture<PeriodicTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PeriodicTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodicTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
