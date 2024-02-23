import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImcapacityNewComponent } from './imcapacity-new.component';

describe('ImcapacityNewComponent', () => {
  let component: ImcapacityNewComponent;
  let fixture: ComponentFixture<ImcapacityNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImcapacityNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImcapacityNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
