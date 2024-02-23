import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeadtimeComponent } from './leadtime.component';

describe('LeadtimeComponent', () => {
  let component: LeadtimeComponent;
  let fixture: ComponentFixture<LeadtimeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LeadtimeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LeadtimeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
