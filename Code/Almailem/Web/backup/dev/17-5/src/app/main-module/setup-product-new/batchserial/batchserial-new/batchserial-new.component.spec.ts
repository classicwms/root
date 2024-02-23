import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchserialNewComponent } from './batchserial-new.component';

describe('BatchserialNewComponent', () => {
  let component: BatchserialNewComponent;
  let fixture: ComponentFixture<BatchserialNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BatchserialNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BatchserialNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
