import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ThreePlComponent } from './three-pl.component';

describe('ThreePlComponent', () => {
  let component: ThreePlComponent;
  let fixture: ComponentFixture<ThreePlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ThreePlComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ThreePlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
