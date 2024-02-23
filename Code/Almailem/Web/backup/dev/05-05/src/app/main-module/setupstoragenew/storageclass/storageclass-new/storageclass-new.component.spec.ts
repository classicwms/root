import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageclassNewComponent } from './storageclass-new.component';

describe('StorageclassNewComponent', () => {
  let component: StorageclassNewComponent;
  let fixture: ComponentFixture<StorageclassNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StorageclassNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StorageclassNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
