import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReplaceASNComponent } from './replace-asn.component';

describe('ReplaceASNComponent', () => {
  let component: ReplaceASNComponent;
  let fixture: ComponentFixture<ReplaceASNComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReplaceASNComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReplaceASNComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
