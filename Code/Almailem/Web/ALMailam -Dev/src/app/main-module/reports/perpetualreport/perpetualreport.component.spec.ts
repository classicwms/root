import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerpetualreportComponent } from './perpetualreport.component';

describe('PerpetualreportComponent', () => {
  let component: PerpetualreportComponent;
  let fixture: ComponentFixture<PerpetualreportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerpetualreportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PerpetualreportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
