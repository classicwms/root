import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinclassidComponent } from './binclassid.component';

describe('BinclassidComponent', () => {
  let component: BinclassidComponent;
  let fixture: ComponentFixture<BinclassidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinclassidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinclassidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
