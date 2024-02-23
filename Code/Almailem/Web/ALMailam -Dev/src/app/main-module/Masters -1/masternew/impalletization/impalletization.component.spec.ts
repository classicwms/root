import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpalletizationComponent } from './impalletization.component';

describe('ImpalletizationComponent', () => {
  let component: ImpalletizationComponent;
  let fixture: ComponentFixture<ImpalletizationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImpalletizationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImpalletizationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
