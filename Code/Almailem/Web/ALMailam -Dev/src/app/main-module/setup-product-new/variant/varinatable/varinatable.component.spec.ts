import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VarinatableComponent } from './varinatable.component';

describe('VarinatableComponent', () => {
  let component: VarinatableComponent;
  let fixture: ComponentFixture<VarinatableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VarinatableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VarinatableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
