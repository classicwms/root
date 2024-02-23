import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsertypeNewComponent } from './usertype-new.component';

describe('UsertypeNewComponent', () => {
  let component: UsertypeNewComponent;
  let fixture: ComponentFixture<UsertypeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UsertypeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UsertypeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
