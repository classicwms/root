import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreoutboundNewComponent } from './preoutbound-new.component';

describe('PreoutboundNewComponent', () => {
  let component: PreoutboundNewComponent;
  let fixture: ComponentFixture<PreoutboundNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreoutboundNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreoutboundNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
