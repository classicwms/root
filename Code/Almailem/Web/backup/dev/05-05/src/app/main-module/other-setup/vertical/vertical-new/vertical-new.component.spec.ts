import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerticalNewComponent } from './vertical-new.component';

describe('VerticalNewComponent', () => {
  let component: VerticalNewComponent;
  let fixture: ComponentFixture<VerticalNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VerticalNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerticalNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
