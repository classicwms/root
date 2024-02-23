import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReDeliveryLineComponent } from './re-delivery-line.component';

describe('ReDeliveryLineComponent', () => {
  let component: ReDeliveryLineComponent;
  let fixture: ComponentFixture<ReDeliveryLineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReDeliveryLineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReDeliveryLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
