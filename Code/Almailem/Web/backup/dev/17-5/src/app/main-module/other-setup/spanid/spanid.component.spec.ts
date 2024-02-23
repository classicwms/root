import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpanidComponent } from './spanid.component';

describe('SpanidComponent', () => {
  let component: SpanidComponent;
  let fixture: ComponentFixture<SpanidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpanidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpanidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
