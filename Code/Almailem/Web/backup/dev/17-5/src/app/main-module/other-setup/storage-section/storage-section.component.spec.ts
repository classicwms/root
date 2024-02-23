import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageSectionComponent } from './storage-section.component';

describe('StorageSectionComponent', () => {
  let component: StorageSectionComponent;
  let fixture: ComponentFixture<StorageSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StorageSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StorageSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
