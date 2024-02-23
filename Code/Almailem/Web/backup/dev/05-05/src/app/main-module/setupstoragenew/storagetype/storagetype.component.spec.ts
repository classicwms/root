import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoragetypeComponent } from './storagetype.component';

describe('StoragetypeComponent', () => {
  let component: StoragetypeComponent;
  let fixture: ComponentFixture<StoragetypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoragetypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoragetypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
