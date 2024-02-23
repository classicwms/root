import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AltpartComponent } from './altpart.component';

describe('AltpartComponent', () => {
  let component: AltpartComponent;
  let fixture: ComponentFixture<AltpartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AltpartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AltpartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
