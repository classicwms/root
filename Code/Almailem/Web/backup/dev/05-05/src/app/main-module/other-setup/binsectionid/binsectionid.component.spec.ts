import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinsectionidComponent } from './binsectionid.component';

describe('BinsectionidComponent', () => {
  let component: BinsectionidComponent;
  let fixture: ComponentFixture<BinsectionidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinsectionidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinsectionidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
