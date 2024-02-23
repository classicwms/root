import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerpetualTabComponent } from './perpetual-tab.component';

describe('PerpetualTabComponent', () => {
  let component: PerpetualTabComponent;
  let fixture: ComponentFixture<PerpetualTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerpetualTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PerpetualTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
