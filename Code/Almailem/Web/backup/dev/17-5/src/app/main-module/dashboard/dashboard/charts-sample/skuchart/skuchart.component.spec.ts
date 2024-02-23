import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkuchartComponent } from './skuchart.component';

describe('SkuchartComponent', () => {
  let component: SkuchartComponent;
  let fixture: ComponentFixture<SkuchartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SkuchartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SkuchartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
