import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ControlprocessidNewComponent } from './controlprocessid-new.component';

describe('ControlprocessidNewComponent', () => {
  let component: ControlprocessidNewComponent;
  let fixture: ComponentFixture<ControlprocessidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ControlprocessidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ControlprocessidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
