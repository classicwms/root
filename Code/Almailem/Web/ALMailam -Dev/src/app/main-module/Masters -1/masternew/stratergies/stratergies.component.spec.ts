import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StratergiesComponent } from './stratergies.component';

describe('StratergiesComponent', () => {
  let component: StratergiesComponent;
  let fixture: ComponentFixture<StratergiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StratergiesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StratergiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
