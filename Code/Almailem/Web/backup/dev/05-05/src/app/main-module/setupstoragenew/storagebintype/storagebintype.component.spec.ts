import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoragebintypeComponent } from './storagebintype.component';

describe('StoragebintypeComponent', () => {
  let component: StoragebintypeComponent;
  let fixture: ComponentFixture<StoragebintypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoragebintypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoragebintypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
