import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupProductNewComponent } from './setup-product-new.component';

describe('SetupProductNewComponent', () => {
  let component: SetupProductNewComponent;
  let fixture: ComponentFixture<SetupProductNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SetupProductNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SetupProductNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
