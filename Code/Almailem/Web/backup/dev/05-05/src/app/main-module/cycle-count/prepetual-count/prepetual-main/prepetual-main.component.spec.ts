import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrepetualMainComponent } from './prepetual-main.component';

describe('PrepetualMainComponent', () => {
  let component: PrepetualMainComponent;
  let fixture: ComponentFixture<PrepetualMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrepetualMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepetualMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
