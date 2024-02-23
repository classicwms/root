import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddlinesComponent } from './addlines.component';

describe('AddlinesComponent', () => {
  let component: AddlinesComponent;
  let fixture: ComponentFixture<AddlinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddlinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddlinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
