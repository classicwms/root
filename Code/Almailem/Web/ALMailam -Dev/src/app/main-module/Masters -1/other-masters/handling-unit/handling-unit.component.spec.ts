import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HandlingUnitComponent } from './handling-unit.component';

describe('HandlingUnitComponent', () => {
  let component: HandlingUnitComponent;
  let fixture: ComponentFixture<HandlingUnitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HandlingUnitComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HandlingUnitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
