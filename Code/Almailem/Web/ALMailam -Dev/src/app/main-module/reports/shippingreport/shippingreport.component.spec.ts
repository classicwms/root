import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShippingreportComponent } from './shippingreport.component';

describe('ShippingreportComponent', () => {
  let component: ShippingreportComponent;
  let fixture: ComponentFixture<ShippingreportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShippingreportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShippingreportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
