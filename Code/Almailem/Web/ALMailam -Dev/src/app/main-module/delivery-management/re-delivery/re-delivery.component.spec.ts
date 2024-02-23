import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReDeliveryComponent } from './re-delivery.component';

describe('ReDeliveryComponent', () => {
  let component: ReDeliveryComponent;
  let fixture: ComponentFixture<ReDeliveryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReDeliveryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReDeliveryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
