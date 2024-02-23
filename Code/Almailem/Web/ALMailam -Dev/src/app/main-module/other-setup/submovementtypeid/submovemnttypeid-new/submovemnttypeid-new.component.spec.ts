import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmovemnttypeidNewComponent } from './submovemnttypeid-new.component';

describe('SubmovemnttypeidNewComponent', () => {
  let component: SubmovemnttypeidNewComponent;
  let fixture: ComponentFixture<SubmovemnttypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubmovemnttypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmovemnttypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
