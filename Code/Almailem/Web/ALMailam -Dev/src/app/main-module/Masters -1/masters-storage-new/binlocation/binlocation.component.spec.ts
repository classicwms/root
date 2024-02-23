import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinlocationComponent } from './binlocation.component';

describe('BinlocationComponent', () => {
  let component: BinlocationComponent;
  let fixture: ComponentFixture<BinlocationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinlocationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinlocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
