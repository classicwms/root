import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AltuomComponent } from './altuom.component';

describe('AltuomComponent', () => {
  let component: AltuomComponent;
  let fixture: ComponentFixture<AltuomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AltuomComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AltuomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
