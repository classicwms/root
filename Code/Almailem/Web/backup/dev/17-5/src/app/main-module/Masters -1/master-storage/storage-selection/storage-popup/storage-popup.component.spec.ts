import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoragePopupComponent } from './storage-popup.component';

describe('StoragePopupComponent', () => {
  let component: StoragePopupComponent;
  let fixture: ComponentFixture<StoragePopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoragePopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoragePopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
