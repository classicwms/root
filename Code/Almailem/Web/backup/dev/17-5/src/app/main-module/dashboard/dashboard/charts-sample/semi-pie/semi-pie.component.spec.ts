import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SemiPieComponent } from './semi-pie.component';

describe('SemiPieComponent', () => {
  let component: SemiPieComponent;
  let fixture: ComponentFixture<SemiPieComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SemiPieComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SemiPieComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
