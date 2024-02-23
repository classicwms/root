import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreinboundeditpopupComponent } from './preinboundeditpopup.component';

describe('PreinboundeditpopupComponent', () => {
  let component: PreinboundeditpopupComponent;
  let fixture: ComponentFixture<PreinboundeditpopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreinboundeditpopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreinboundeditpopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
