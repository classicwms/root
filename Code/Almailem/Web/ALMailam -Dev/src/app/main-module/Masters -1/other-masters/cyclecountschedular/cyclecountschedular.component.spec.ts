import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CyclecountschedularComponent } from './cyclecountschedular.component';

describe('CyclecountschedularComponent', () => {
  let component: CyclecountschedularComponent;
  let fixture: ComponentFixture<CyclecountschedularComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CyclecountschedularComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CyclecountschedularComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
