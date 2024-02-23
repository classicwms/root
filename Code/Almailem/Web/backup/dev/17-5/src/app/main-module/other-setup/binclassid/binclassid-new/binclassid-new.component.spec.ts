import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinclassidNewComponent } from './binclassid-new.component';

describe('BinclassidNewComponent', () => {
  let component: BinclassidNewComponent;
  let fixture: ComponentFixture<BinclassidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinclassidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinclassidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
