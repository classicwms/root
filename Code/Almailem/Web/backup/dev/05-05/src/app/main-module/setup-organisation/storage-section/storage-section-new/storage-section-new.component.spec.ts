import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageSectionNewComponent } from './storage-section-new.component';

describe('StorageSectionNewComponent', () => {
  let component: StorageSectionNewComponent;
  let fixture: ComponentFixture<StorageSectionNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StorageSectionNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StorageSectionNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
