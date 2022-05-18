import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReallocationComponent } from './reallocation.component';

describe('ReallocationComponent', () => {
  let component: ReallocationComponent;
  let fixture: ComponentFixture<ReallocationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReallocationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReallocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
