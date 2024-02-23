import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinEmptyComponent } from './bin-empty.component';

describe('BinEmptyComponent', () => {
  let component: BinEmptyComponent;
  let fixture: ComponentFixture<BinEmptyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinEmptyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinEmptyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
