import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreinboundUploadComponent } from './preinbound-upload.component';

describe('PreinboundUploadComponent', () => {
  let component: PreinboundUploadComponent;
  let fixture: ComponentFixture<PreinboundUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreinboundUploadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreinboundUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
