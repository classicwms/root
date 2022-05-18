import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReversalMainComponent } from './reversal-main.component';

describe('ReversalMainComponent', () => {
  let component: ReversalMainComponent;
  let fixture: ComponentFixture<ReversalMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReversalMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReversalMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
