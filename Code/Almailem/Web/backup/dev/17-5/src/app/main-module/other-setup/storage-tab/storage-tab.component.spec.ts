import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageTabComponent } from './storage-tab.component';

describe('StorageTabComponent', () => {
  let component: StorageTabComponent;
  let fixture: ComponentFixture<StorageTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StorageTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StorageTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
