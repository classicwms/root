import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusidComponent } from './statusid.component';

describe('StatusidComponent', () => {
  let component: StatusidComponent;
  let fixture: ComponentFixture<StatusidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StatusidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
