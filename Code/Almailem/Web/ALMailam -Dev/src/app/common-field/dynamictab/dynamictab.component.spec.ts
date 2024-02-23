import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamictabComponent } from './dynamictab.component';

describe('DynamictabComponent', () => {
  let component: DynamictabComponent;
  let fixture: ComponentFixture<DynamictabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DynamictabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DynamictabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
