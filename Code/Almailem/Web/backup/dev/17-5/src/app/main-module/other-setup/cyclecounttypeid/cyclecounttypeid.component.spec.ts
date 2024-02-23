import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CyclecounttypeidComponent } from './cyclecounttypeid.component';

describe('CyclecounttypeidComponent', () => {
  let component: CyclecounttypeidComponent;
  let fixture: ComponentFixture<CyclecounttypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CyclecounttypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CyclecounttypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
