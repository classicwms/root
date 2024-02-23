import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverNewComponent } from './driver-new.component';

describe('DriverNewComponent', () => {
  let component: DriverNewComponent;
  let fixture: ComponentFixture<DriverNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DriverNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
