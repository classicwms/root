import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpalletizationNewComponent } from './impalletization-new.component';

describe('ImpalletizationNewComponent', () => {
  let component: ImpalletizationNewComponent;
  let fixture: ComponentFixture<ImpalletizationNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImpalletizationNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImpalletizationNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
