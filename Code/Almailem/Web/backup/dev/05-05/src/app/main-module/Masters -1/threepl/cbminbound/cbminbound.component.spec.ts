import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CbminboundComponent } from './cbminbound.component';

describe('CbminboundComponent', () => {
  let component: CbminboundComponent;
  let fixture: ComponentFixture<CbminboundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CbminboundComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CbminboundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
