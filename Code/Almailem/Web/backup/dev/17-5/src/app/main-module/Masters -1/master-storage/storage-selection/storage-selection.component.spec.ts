import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageSelectionComponent } from './storage-selection.component';

describe('StorageSelectionComponent', () => {
  let component: StorageSelectionComponent;
  let fixture: ComponentFixture<StorageSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StorageSelectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StorageSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
