import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteNewComponent } from './route-new.component';

describe('RouteNewComponent', () => {
  let component: RouteNewComponent;
  let fixture: ComponentFixture<RouteNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RouteNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RouteNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
