import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceidNewComponent } from './serviceid-new.component';

describe('ServiceidNewComponent', () => {
  let component: ServiceidNewComponent;
  let fixture: ComponentFixture<ServiceidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiceidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
