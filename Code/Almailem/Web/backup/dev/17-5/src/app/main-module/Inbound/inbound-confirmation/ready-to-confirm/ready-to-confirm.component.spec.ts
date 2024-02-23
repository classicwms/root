import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReadyToConfirmComponent } from './ready-to-confirm.component';

describe('ReadyToConfirmComponent', () => {
  let component: ReadyToConfirmComponent;
  let fixture: ComponentFixture<ReadyToConfirmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReadyToConfirmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadyToConfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
