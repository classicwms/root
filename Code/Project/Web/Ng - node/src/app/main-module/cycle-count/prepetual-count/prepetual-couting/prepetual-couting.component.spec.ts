import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrepetualCoutingComponent } from './prepetual-couting.component';

describe('PrepetualCoutingComponent', () => {
  let component: PrepetualCoutingComponent;
  let fixture: ComponentFixture<PrepetualCoutingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrepetualCoutingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepetualCoutingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
