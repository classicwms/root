import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefdoctypeidNewComponent } from './refdoctypeid-new.component';

describe('RefdoctypeidNewComponent', () => {
  let component: RefdoctypeidNewComponent;
  let fixture: ComponentFixture<RefdoctypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RefdoctypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RefdoctypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
