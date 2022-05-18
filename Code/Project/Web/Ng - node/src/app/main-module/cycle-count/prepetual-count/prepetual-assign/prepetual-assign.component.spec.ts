import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrepetualAssignComponent } from './prepetual-assign.component';

describe('PrepetualAssignComponent', () => {
  let component: PrepetualAssignComponent;
  let fixture: ComponentFixture<PrepetualAssignComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrepetualAssignComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepetualAssignComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
