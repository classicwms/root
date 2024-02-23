import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StratergyComponent } from './stratergy.component';

describe('StratergyComponent', () => {
  let component: StratergyComponent;
  let fixture: ComponentFixture<StratergyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StratergyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StratergyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
