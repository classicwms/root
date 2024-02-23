import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReturntypeidComponent } from './returntypeid.component';

describe('ReturntypeidComponent', () => {
  let component: ReturntypeidComponent;
  let fixture: ComponentFixture<ReturntypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReturntypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReturntypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
