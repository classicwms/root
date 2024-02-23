import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OthermasterTabComponent } from './othermaster-tab.component';

describe('OthermasterTabComponent', () => {
  let component: OthermasterTabComponent;
  let fixture: ComponentFixture<OthermasterTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OthermasterTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OthermasterTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
