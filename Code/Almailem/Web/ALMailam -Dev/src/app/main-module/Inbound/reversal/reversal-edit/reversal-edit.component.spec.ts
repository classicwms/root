import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReversalEditComponent } from './reversal-edit.component';

describe('ReversalEditComponent', () => {
  let component: ReversalEditComponent;
  let fixture: ComponentFixture<ReversalEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReversalEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReversalEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
