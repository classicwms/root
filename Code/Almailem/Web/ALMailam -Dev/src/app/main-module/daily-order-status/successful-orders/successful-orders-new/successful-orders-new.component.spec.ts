import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuccessfulOrdersNewComponent } from './successful-orders-new.component';

describe('SuccessfulOrdersNewComponent', () => {
  let component: SuccessfulOrdersNewComponent;
  let fixture: ComponentFixture<SuccessfulOrdersNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SuccessfulOrdersNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuccessfulOrdersNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
