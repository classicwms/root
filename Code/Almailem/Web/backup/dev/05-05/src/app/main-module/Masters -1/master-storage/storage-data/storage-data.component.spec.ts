import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageDataComponent } from './storage-data.component';

describe('StorageDataComponent', () => {
  let component: StorageDataComponent;
  let fixture: ComponentFixture<StorageDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StorageDataComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StorageDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
