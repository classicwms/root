import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MiddlewareTabComponent } from './middleware-tab.component';

describe('MiddlewareTabComponent', () => {
  let component: MiddlewareTabComponent;
  let fixture: ComponentFixture<MiddlewareTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MiddlewareTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MiddlewareTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
