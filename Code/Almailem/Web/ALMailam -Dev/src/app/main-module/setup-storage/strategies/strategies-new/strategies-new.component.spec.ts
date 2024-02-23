import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StrategiesNewComponent } from './strategies-new.component';

describe('StrategiesNewComponent', () => {
  let component: StrategiesNewComponent;
  let fixture: ComponentFixture<StrategiesNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StrategiesNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StrategiesNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
