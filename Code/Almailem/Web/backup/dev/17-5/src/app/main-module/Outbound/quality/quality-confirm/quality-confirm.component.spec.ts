import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QualityConfirmComponent } from './quality-confirm.component';

describe('QualityConfirmComponent', () => {
  let component: QualityConfirmComponent;
  let fixture: ComponentFixture<QualityConfirmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QualityConfirmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QualityConfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
