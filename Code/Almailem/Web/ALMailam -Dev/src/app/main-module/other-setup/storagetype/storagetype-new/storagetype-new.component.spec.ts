import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoragetypeNewComponent } from './storagetype-new.component';

describe('StoragetypeNewComponent', () => {
  let component: StoragetypeNewComponent;
  let fixture: ComponentFixture<StoragetypeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoragetypeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoragetypeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
