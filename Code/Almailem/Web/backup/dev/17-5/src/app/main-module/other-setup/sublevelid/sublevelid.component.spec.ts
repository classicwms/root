import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SublevelidComponent } from './sublevelid.component';

describe('SublevelidComponent', () => {
  let component: SublevelidComponent;
  let fixture: ComponentFixture<SublevelidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SublevelidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SublevelidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
