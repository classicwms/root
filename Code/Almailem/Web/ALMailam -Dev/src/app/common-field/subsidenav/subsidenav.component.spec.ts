import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubsidenavComponent } from './subsidenav.component';

describe('SubsidenavComponent', () => {
  let component: SubsidenavComponent;
  let fixture: ComponentFixture<SubsidenavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubsidenavComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubsidenavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
