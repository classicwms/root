import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefdoctypeidComponent } from './refdoctypeid.component';

describe('RefdoctypeidComponent', () => {
  let component: RefdoctypeidComponent;
  let fixture: ComponentFixture<RefdoctypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RefdoctypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RefdoctypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
