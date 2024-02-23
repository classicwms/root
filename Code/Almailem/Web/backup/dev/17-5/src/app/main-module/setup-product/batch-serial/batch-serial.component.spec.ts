import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchSerialComponent } from './batch-serial.component';

describe('BatchSerialComponent', () => {
  let component: BatchSerialComponent;
  let fixture: ComponentFixture<BatchSerialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BatchSerialComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BatchSerialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
