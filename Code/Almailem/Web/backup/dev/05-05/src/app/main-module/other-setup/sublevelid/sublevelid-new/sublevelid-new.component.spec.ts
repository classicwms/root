import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SublevelidNewComponent } from './sublevelid-new.component';

describe('SublevelidNewComponent', () => {
  let component: SublevelidNewComponent;
  let fixture: ComponentFixture<SublevelidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SublevelidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SublevelidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
