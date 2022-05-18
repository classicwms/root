import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QualityMainComponent } from './quality-main.component';

describe('QualityMainComponent', () => {
  let component: QualityMainComponent;
  let fixture: ComponentFixture<QualityMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QualityMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QualityMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
