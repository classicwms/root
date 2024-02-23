import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CyclecountschedularNewComponent } from './cyclecountschedular-new.component';

describe('CyclecountschedularNewComponent', () => {
  let component: CyclecountschedularNewComponent;
  let fixture: ComponentFixture<CyclecountschedularNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CyclecountschedularNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CyclecountschedularNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
