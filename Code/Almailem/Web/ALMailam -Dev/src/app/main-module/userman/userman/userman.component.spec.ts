import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsermanComponent } from './userman.component';

describe('UsermanComponent', () => {
  let component: UsermanComponent;
  let fixture: ComponentFixture<UsermanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UsermanComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UsermanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
