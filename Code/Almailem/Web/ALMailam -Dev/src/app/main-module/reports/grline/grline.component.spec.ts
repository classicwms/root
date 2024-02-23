import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GrlineComponent } from './grline.component';

describe('GrlineComponent', () => {
  let component: GrlineComponent;
  let fixture: ComponentFixture<GrlineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GrlineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GrlineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
