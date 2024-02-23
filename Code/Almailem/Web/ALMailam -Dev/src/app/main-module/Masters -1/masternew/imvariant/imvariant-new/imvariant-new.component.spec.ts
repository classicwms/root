import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImvariantNewComponent } from './imvariant-new.component';

describe('ImvariantNewComponent', () => {
  let component: ImvariantNewComponent;
  let fixture: ComponentFixture<ImvariantNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImvariantNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImvariantNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
