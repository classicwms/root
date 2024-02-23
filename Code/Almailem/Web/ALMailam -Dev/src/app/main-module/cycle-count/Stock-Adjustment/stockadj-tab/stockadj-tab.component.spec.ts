import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockadjTabComponent } from './stockadj-tab.component';

describe('StockadjTabComponent', () => {
  let component: StockadjTabComponent;
  let fixture: ComponentFixture<StockadjTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StockadjTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StockadjTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
