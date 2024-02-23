import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StratergyNewComponent } from './stratergy-new.component';

describe('StratergyNewComponent', () => {
  let component: StratergyNewComponent;
  let fixture: ComponentFixture<StratergyNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StratergyNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StratergyNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
