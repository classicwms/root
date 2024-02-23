import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceidComponent } from './serviceid.component';

describe('ServiceidComponent', () => {
  let component: ServiceidComponent;
  let fixture: ComponentFixture<ServiceidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiceidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
