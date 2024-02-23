import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LabelGenerateComponent } from './label-generate.component';

describe('LabelGenerateComponent', () => {
  let component: LabelGenerateComponent;
  let fixture: ComponentFixture<LabelGenerateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LabelGenerateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LabelGenerateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
