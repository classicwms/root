import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PicklistHeaderComponent } from './picklist-header.component';

describe('PicklistHeaderComponent', () => {
  let component: PicklistHeaderComponent;
  let fixture: ComponentFixture<PicklistHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PicklistHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PicklistHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
