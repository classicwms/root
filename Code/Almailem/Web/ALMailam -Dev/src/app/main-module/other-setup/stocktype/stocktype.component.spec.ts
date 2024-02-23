import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StocktypeComponent } from './stocktype.component';

describe('StocktypeComponent', () => {
  let component: StocktypeComponent;
  let fixture: ComponentFixture<StocktypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StocktypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StocktypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
