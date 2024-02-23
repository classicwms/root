import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecimalnotationidNewComponent } from './decimalnotationid-new.component';

describe('DecimalnotationidNewComponent', () => {
  let component: DecimalnotationidNewComponent;
  let fixture: ComponentFixture<DecimalnotationidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DecimalnotationidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DecimalnotationidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
