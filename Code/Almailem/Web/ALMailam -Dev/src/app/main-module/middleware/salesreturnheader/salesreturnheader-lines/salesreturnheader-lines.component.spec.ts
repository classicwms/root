import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesreturnheaderLinesComponent } from './salesreturnheader-lines.component';

describe('SalesreturnheaderLinesComponent', () => {
  let component: SalesreturnheaderLinesComponent;
  let fixture: ComponentFixture<SalesreturnheaderLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalesreturnheaderLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesreturnheaderLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
