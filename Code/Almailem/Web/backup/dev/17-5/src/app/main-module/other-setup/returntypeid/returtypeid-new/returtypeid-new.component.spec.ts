import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReturtypeidNewComponent } from './returtypeid-new.component';

describe('ReturtypeidNewComponent', () => {
  let component: ReturtypeidNewComponent;
  let fixture: ComponentFixture<ReturtypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReturtypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReturtypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
