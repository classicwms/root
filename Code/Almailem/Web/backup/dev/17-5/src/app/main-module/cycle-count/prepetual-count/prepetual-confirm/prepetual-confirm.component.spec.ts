import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrepetualConfirmComponent } from './prepetual-confirm.component';

describe('PrepetualConfirmComponent', () => {
  let component: PrepetualConfirmComponent;
  let fixture: ComponentFixture<PrepetualConfirmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrepetualConfirmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepetualConfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
