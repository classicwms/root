import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarcodetypeidComponent } from './barcodetypeid.component';

describe('BarcodetypeidComponent', () => {
  let component: BarcodetypeidComponent;
  let fixture: ComponentFixture<BarcodetypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BarcodetypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BarcodetypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
