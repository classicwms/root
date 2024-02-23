import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateformatidComponent } from './dateformatid.component';

describe('DateformatidComponent', () => {
  let component: DateformatidComponent;
  let fixture: ComponentFixture<DateformatidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DateformatidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateformatidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
