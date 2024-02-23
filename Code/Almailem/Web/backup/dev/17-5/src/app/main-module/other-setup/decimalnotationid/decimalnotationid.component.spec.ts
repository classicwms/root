import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecimalnotationidComponent } from './decimalnotationid.component';

describe('DecimalnotationidComponent', () => {
  let component: DecimalnotationidComponent;
  let fixture: ComponentFixture<DecimalnotationidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DecimalnotationidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DecimalnotationidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
