import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveryNewComponent } from './delivery-new.component';

describe('DeliveryNewComponent', () => {
  let component: DeliveryNewComponent;
  let fixture: ComponentFixture<DeliveryNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliveryNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliveryNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
