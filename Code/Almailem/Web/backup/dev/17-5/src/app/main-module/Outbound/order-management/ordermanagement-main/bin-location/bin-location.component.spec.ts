import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinLocationComponent } from './bin-location.component';

describe('BinLocationComponent', () => {
  let component: BinLocationComponent;
  let fixture: ComponentFixture<BinLocationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinLocationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinLocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
