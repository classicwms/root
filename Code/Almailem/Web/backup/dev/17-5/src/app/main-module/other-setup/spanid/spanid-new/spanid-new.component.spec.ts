import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpanidNewComponent } from './spanid-new.component';

describe('SpanidNewComponent', () => {
  let component: SpanidNewComponent;
  let fixture: ComponentFixture<SpanidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpanidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpanidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
