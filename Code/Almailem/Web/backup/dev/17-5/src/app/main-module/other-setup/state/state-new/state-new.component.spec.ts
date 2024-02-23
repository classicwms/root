import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StateNewComponent } from './state-new.component';

describe('StateNewComponent', () => {
  let component: StateNewComponent;
  let fixture: ComponentFixture<StateNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StateNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StateNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
