import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CbminboundNewComponent } from './cbminbound-new.component';

describe('CbminboundNewComponent', () => {
  let component: CbminboundNewComponent;
  let fixture: ComponentFixture<CbminboundNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CbminboundNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CbminboundNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
