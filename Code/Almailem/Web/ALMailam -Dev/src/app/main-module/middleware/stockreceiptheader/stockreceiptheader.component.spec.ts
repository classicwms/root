import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockreceiptheaderComponent } from './stockreceiptheader.component';

describe('StockreceiptheaderComponent', () => {
  let component: StockreceiptheaderComponent;
  let fixture: ComponentFixture<StockreceiptheaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StockreceiptheaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StockreceiptheaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
