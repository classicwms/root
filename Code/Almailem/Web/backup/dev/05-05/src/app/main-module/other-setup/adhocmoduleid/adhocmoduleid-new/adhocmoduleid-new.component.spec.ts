import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdhocmoduleidNewComponent } from './adhocmoduleid-new.component';

describe('AdhocmoduleidNewComponent', () => {
  let component: AdhocmoduleidNewComponent;
  let fixture: ComponentFixture<AdhocmoduleidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdhocmoduleidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdhocmoduleidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
