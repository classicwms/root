import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecialstockindicatorNewComponent } from './specialstockindicator-new.component';

describe('SpecialstockindicatorNewComponent', () => {
  let component: SpecialstockindicatorNewComponent;
  let fixture: ComponentFixture<SpecialstockindicatorNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpecialstockindicatorNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpecialstockindicatorNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
