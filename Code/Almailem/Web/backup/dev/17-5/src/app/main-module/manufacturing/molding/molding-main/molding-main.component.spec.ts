import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoldingMainComponent } from './molding-main.component';

describe('MoldingMainComponent', () => {
  let component: MoldingMainComponent;
  let fixture: ComponentFixture<MoldingMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MoldingMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MoldingMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
