import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ThreeplComponent } from './threepl.component';

describe('ThreeplComponent', () => {
  let component: ThreeplComponent;
  let fixture: ComponentFixture<ThreeplComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ThreeplComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ThreeplComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
