import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StocktypeNewComponent } from './stocktype-new.component';

describe('StocktypeNewComponent', () => {
  let component: StocktypeNewComponent;
  let fixture: ComponentFixture<StocktypeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StocktypeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StocktypeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
