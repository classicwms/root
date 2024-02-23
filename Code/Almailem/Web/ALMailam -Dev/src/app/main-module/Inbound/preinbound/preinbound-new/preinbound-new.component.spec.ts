import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreinboundNewComponent } from './preinbound-new.component';

describe('PreinboundNewComponent', () => {
  let component: PreinboundNewComponent;
  let fixture: ComponentFixture<PreinboundNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreinboundNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreinboundNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
