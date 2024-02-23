import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecialstockindicatorComponent } from './specialstockindicator.component';

describe('SpecialstockindicatorComponent', () => {
  let component: SpecialstockindicatorComponent;
  let fixture: ComponentFixture<SpecialstockindicatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpecialstockindicatorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpecialstockindicatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
