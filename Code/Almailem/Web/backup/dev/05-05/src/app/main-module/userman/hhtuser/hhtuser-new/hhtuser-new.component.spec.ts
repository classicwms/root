import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HhtuserNewComponent } from './hhtuser-new.component';

describe('HhtuserNewComponent', () => {
  let component: HhtuserNewComponent;
  let fixture: ComponentFixture<HhtuserNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HhtuserNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HhtuserNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
