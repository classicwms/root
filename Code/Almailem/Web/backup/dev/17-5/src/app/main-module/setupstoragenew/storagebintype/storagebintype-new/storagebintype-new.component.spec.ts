import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoragebintypeNewComponent } from './storagebintype-new.component';

describe('StoragebintypeNewComponent', () => {
  let component: StoragebintypeNewComponent;
  let fixture: ComponentFixture<StoragebintypeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoragebintypeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoragebintypeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
