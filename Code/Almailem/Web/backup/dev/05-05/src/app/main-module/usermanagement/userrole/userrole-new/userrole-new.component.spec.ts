import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserroleNewComponent } from './userrole-new.component';

describe('UserroleNewComponent', () => {
  let component: UserroleNewComponent;
  let fixture: ComponentFixture<UserroleNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserroleNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserroleNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
