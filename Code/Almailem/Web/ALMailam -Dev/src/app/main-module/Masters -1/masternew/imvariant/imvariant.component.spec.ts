import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImvariantComponent } from './imvariant.component';

describe('ImvariantComponent', () => {
  let component: ImvariantComponent;
  let fixture: ComponentFixture<ImvariantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImvariantComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImvariantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
