import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UomNewComponent } from './uom-new.component';

describe('UomNewComponent', () => {
  let component: UomNewComponent;
  let fixture: ComponentFixture<UomNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UomNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UomNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
