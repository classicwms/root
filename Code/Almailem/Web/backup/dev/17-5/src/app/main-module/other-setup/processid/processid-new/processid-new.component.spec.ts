import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessidNewComponent } from './processid-new.component';

describe('ProcessidNewComponent', () => {
  let component: ProcessidNewComponent;
  let fixture: ComponentFixture<ProcessidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
