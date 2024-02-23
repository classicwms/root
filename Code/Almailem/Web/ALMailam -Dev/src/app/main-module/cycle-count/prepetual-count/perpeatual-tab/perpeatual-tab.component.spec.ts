import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerpeatualTabComponent } from './perpeatual-tab.component';

describe('PerpeatualTabComponent', () => {
  let component: PerpeatualTabComponent;
  let fixture: ComponentFixture<PerpeatualTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerpeatualTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PerpeatualTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
