import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageTypeComponent } from './storage-type.component';

describe('StorageTypeComponent', () => {
  let component: StorageTypeComponent;
  let fixture: ComponentFixture<StorageTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StorageTypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StorageTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
