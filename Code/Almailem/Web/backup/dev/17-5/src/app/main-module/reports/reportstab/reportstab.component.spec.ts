import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportstabComponent } from './reportstab.component';

describe('ReportstabComponent', () => {
  let component: ReportstabComponent;
  let fixture: ComponentFixture<ReportstabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReportstabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportstabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
