import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesreturnheaderComponent } from './salesreturnheader.component';

describe('SalesreturnheaderComponent', () => {
  let component: SalesreturnheaderComponent;
  let fixture: ComponentFixture<SalesreturnheaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalesreturnheaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesreturnheaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
