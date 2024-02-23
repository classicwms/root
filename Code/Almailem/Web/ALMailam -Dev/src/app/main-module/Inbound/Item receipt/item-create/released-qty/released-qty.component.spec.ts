import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReleasedQtyComponent } from './released-qty.component';

describe('ReleasedQtyComponent', () => {
  let component: ReleasedQtyComponent;
  let fixture: ComponentFixture<ReleasedQtyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReleasedQtyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReleasedQtyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
