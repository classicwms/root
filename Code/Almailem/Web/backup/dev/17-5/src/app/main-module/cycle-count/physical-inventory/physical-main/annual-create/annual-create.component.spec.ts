import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnualCreateComponent } from './annual-create.component';

describe('AnnualCreateComponent', () => {
  let component: AnnualCreateComponent;
  let fixture: ComponentFixture<AnnualCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnnualCreateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnnualCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
