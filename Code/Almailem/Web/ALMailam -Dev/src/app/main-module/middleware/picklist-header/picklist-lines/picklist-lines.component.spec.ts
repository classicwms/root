import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PicklistLinesComponent } from './picklist-lines.component';

describe('PicklistLinesComponent', () => {
  let component: PicklistLinesComponent;
  let fixture: ComponentFixture<PicklistLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PicklistLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PicklistLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
