import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoragebinTableComponent } from './storagebin-table.component';

describe('StoragebinTableComponent', () => {
  let component: StoragebinTableComponent;
  let fixture: ComponentFixture<StoragebinTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoragebinTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoragebinTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
