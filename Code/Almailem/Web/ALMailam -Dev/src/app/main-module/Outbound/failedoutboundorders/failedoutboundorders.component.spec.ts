import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FailedoutboundordersComponent } from './failedoutboundorders.component';

describe('FailedoutboundordersComponent', () => {
  let component: FailedoutboundordersComponent;
  let fixture: ComponentFixture<FailedoutboundordersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FailedoutboundordersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FailedoutboundordersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
