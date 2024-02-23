import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpackingComponent } from './impacking.component';

describe('ImpackingComponent', () => {
  let component: ImpackingComponent;
  let fixture: ComponentFixture<ImpackingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImpackingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImpackingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
