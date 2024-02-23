import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChilditemcodeComponent } from './childitemcode.component';

describe('ChilditemcodeComponent', () => {
  let component: ChilditemcodeComponent;
  let fixture: ComponentFixture<ChilditemcodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChilditemcodeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChilditemcodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
