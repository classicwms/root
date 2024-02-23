import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinFullComponent } from './bin-full.component';

describe('BinFullComponent', () => {
  let component: BinFullComponent;
  let fixture: ComponentFixture<BinFullComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinFullComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinFullComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
