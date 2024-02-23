import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessNewComponent } from './process-new.component';

describe('ProcessNewComponent', () => {
  let component: ProcessNewComponent;
  let fixture: ComponentFixture<ProcessNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
