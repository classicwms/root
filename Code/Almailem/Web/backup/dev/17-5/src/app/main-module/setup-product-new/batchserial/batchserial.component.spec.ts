import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchserialComponent } from './batchserial.component';

describe('BatchserialComponent', () => {
  let component: BatchserialComponent;
  let fixture: ComponentFixture<BatchserialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BatchserialComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BatchserialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
