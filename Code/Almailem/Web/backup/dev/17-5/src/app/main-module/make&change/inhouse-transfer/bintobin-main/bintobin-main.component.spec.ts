import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BintobinMainComponent } from './bintobin-main.component';

describe('BintobinMainComponent', () => {
  let component: BintobinMainComponent;
  let fixture: ComponentFixture<BintobinMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BintobinMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BintobinMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
