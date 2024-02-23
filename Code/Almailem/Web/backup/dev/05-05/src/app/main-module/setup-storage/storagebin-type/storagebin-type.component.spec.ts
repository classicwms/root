import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoragebinTypeComponent } from './storagebin-type.component';

describe('StoragebinTypeComponent', () => {
  let component: StoragebinTypeComponent;
  let fixture: ComponentFixture<StoragebinTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoragebinTypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoragebinTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
