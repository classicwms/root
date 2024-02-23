import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinsectionidNewComponent } from './binsectionid-new.component';

describe('BinsectionidNewComponent', () => {
  let component: BinsectionidNewComponent;
  let fixture: ComponentFixture<BinsectionidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinsectionidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinsectionidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
