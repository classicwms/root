import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpackingNewComponent } from './impacking-new.component';

describe('ImpackingNewComponent', () => {
  let component: ImpackingNewComponent;
  let fixture: ComponentFixture<ImpackingNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImpackingNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImpackingNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
