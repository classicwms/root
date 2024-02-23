import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddLinesComponent } from './add-lines.component';

describe('AddLinesComponent', () => {
  let component: AddLinesComponent;
  let fixture: ComponentFixture<AddLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
