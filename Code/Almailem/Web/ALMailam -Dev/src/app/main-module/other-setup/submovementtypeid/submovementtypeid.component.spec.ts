import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmovementtypeidComponent } from './submovementtypeid.component';

describe('SubmovementtypeidComponent', () => {
  let component: SubmovementtypeidComponent;
  let fixture: ComponentFixture<SubmovementtypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubmovementtypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmovementtypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
