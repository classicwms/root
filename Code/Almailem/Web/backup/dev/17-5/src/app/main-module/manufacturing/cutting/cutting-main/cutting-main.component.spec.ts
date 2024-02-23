import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CuttingMainComponent } from './cutting-main.component';

describe('CuttingMainComponent', () => {
  let component: CuttingMainComponent;
  let fixture: ComponentFixture<CuttingMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CuttingMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CuttingMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
