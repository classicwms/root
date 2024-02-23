import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdhocmoduleidComponent } from './adhocmoduleid.component';

describe('AdhocmoduleidComponent', () => {
  let component: AdhocmoduleidComponent;
  let fixture: ComponentFixture<AdhocmoduleidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdhocmoduleidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdhocmoduleidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
